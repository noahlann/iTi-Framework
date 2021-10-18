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

package org.lan.iti.cloud.security.config.annotation.web.builders;

import org.lan.iti.cloud.security.context.AnonymousAuthenticationFilter;
import org.lan.iti.cloud.security.context.SecurityContextPersistenceFilter;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

/**
 * Filter 排序
 * <p>
 * 内部调用
 *
 * @author NorthLan
 * @date 2021/10/15
 * @url https://blog.noahlan.com
 */
public class FilterOrderRegistration {
    private static final int INITIAL_ORDER = 100;

    private static final int ORDER_STEP = 100;

    private final Map<String, Integer> filterToOrder = new HashMap<>();

    public FilterOrderRegistration() {
        Step order = new Step(INITIAL_ORDER, ORDER_STEP);
        put(SecurityContextPersistenceFilter.class, order.next());
        put(CorsFilter.class, order.next());
        // TODO CsrfFilter
        order.next();
        // TODO ConcurrentSessionFilter
        // TODO BearerTokenAuthenticationFilter

        // TODO RequestCacheAwareFilter
        // TODO SecurityContextHolderAwareRequestFilter
        put(AnonymousAuthenticationFilter.class, order.next());
        // TODO SessionManagementFilter

        // TODO FilterSecurityInterceptor
        // TODO AuthorizationFilter
        // TODO SwitchUserFilter
    }

    /**
     * Register a {@link Filter} with its specific position. If the {@link Filter} was
     * already registered before, the position previously defined is not going to be
     * overriden
     *
     * @param filter   the {@link Filter} to register
     * @param position the position to associate with the {@link Filter}
     */
    public void put(Class<? extends Filter> filter, int position) {
        String className = filter.getName();
        if (this.filterToOrder.containsKey(className)) {
            return;
        }
        this.filterToOrder.put(className, position);
    }

    /**
     * Gets the order of a particular {@link Filter} class taking into consideration
     * superclasses.
     *
     * @param clazz the {@link Filter} class to determine the sort order
     * @return the sort order or null if not defined
     */
    public Integer getOrder(Class<?> clazz) {
        while (clazz != null) {
            Integer result = this.filterToOrder.get(clazz.getName());
            if (result != null) {
                return result;
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    private static class Step {
        private int value;
        private final int stepSize;

        Step(int initialValue, int stepSize) {
            this.value = initialValue;
            this.stepSize = stepSize;
        }

        int next() {
            int value = this.value;
            this.value += this.stepSize;
            return value;
        }
    }
}
