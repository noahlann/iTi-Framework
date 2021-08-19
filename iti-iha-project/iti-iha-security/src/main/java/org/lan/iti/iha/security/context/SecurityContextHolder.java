/*
 *
 *  * Copyright (c) [2019-2021] [NorthLan](lan6995@gmail.com)
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.lan.iti.iha.security.context;

import org.lan.iti.common.core.util.ReflectUtil;
import org.lan.iti.common.core.util.StringUtil;

import java.lang.reflect.Constructor;

/**
 * SecurityContextHolder
 *
 * @author NorthLan
 * @date 2021/7/29
 * @url https://blog.noahlan.com
 */
public class SecurityContextHolder {
    public static final String MODE_THREAD_LOCAL = "MODE_THREAD_LOCAL";

    public static final String MODE_INHERITABLE_THREAD_LOCAL = "MODE_INHERITABLE_THREAD_LOCAL";

    public static final String MODE_GLOBAL = "MODE_GLOBAL";

    public static final String SYSTEM_PROPERTY = "iha.security.strategy";

    private static String strategyName = System.getProperty(SYSTEM_PROPERTY);

    private static SecurityContextHolderStrategy strategy;

    private static int initializeCount = 0;

    static {
        initialize();
    }

    private static void initialize() {
        if (!StringUtil.isNotEmpty(strategyName)) {
            // Set default
            strategyName = MODE_THREAD_LOCAL;
        }
        if (strategyName.equals(MODE_THREAD_LOCAL)) {
            strategy = new ThreadLocalSecurityContextHolderStrategy();
        } else if (strategyName.equals(MODE_INHERITABLE_THREAD_LOCAL)) {
            strategy = new InheritableThreadLocalSecurityContextHolderStrategy();
        } else if (strategyName.equals(MODE_GLOBAL)) {
            strategy = new GlobalSecurityContextHolderStrategy();
        } else {
            // Try to load a custom strategy
            try {
                Class<?> clazz = Class.forName(strategyName);
                Constructor<?> customStrategy = clazz.getConstructor();
                strategy = (SecurityContextHolderStrategy) customStrategy.newInstance();
            } catch (Exception ex) {
                ReflectUtil.handleReflectionException(ex);
            }
        }
        initializeCount++;
    }

    /**
     * Explicitly clears the context value from the current thread.
     */
    public static void clearContext() {
        strategy.clearContext();
    }

    /**
     * Obtain the current <code>SecurityContext</code>.
     *
     * @return the security context (never <code>null</code>)
     */
    public static SecurityContext getContext() {
        return strategy.getContext();
    }

    /**
     * Primarily for troubleshooting purposes, this method shows how many times the class
     * has re-initialized its <code>SecurityContextHolderStrategy</code>.
     *
     * @return the count (should be one unless you've called
     * {@link #setStrategyName(String)} to switch to an alternate strategy.
     */
    public static int getInitializeCount() {
        return initializeCount;
    }

    /**
     * Associates a new <code>SecurityContext</code> with the current thread of execution.
     *
     * @param context the new <code>SecurityContext</code> (may not be <code>null</code>)
     */
    public static void setContext(SecurityContext context) {
        strategy.setContext(context);
    }

    /**
     * Changes the preferred strategy. Do <em>NOT</em> call this method more than once for
     * a given JVM, as it will re-initialize the strategy and adversely affect any
     * existing threads using the old strategy.
     *
     * @param strategyName the fully qualified class name of the strategy that should be
     *                     used.
     */
    public static void setStrategyName(String strategyName) {
        SecurityContextHolder.strategyName = strategyName;
        initialize();
    }

    /**
     * Allows retrieval of the context strategy. See SEC-1188.
     *
     * @return the configured strategy for storing the security context.
     */
    public static SecurityContextHolderStrategy getContextHolderStrategy() {
        return strategy;
    }

    /**
     * Delegates the creation of a new, empty context to the configured strategy.
     */
    public static SecurityContext createEmptyContext() {
        return strategy.createEmptyContext();
    }
}
