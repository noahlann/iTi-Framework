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

package org.lan.iti.cloud.strategy.extension;

import org.lan.iti.cloud.strategy.IStrategy;
import org.lan.iti.cloud.strategy.annotation.Strategy;
import org.lan.iti.common.extension.adapter.AbstractExtensionAdapter;
import org.lan.iti.common.extension.adapter.parameter.ExtensionAdapterParameter;
import org.lan.iti.common.extension.support.NamedClassCache;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.function.Function;

/**
 * @author NorthLan
 * @date 2021-07-12
 * @url https://noahlan.com
 */
public class StrategyExtensionAdapter extends AbstractExtensionAdapter {
    private ApplicationContext applicationContext;

    public StrategyExtensionAdapter(Function<Class<?>, NamedClassCache<Class<?>>> getExtensionClasses,
                                    Function<Class<?>, NamedClassCache<Object>> getExtensionInstances) {
        super(getExtensionClasses, getExtensionInstances);
    }

    @Override
    public boolean matches(Object params) {
        return applicationContext != null;
    }

    @Override
    protected void loadParameter(ExtensionAdapterParameter parameter) {
        this.applicationContext = parameter.getByKey(StrategyExtensionConstants.KEY_APPLICATION_CONTEXT);
    }

    @Override
    protected void reset() {
        this.applicationContext = null;
    }

    @Override
    public void init() {
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(Strategy.class);
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            String beanName = entry.getKey();
            Object bean = entry.getValue();
            Class<?> beanClass = entry.getValue().getClass();
            Class<?> interfaceClass = null;
            for (Class<?> clazz : beanClass.getInterfaces()) {
                if (!clazz.getName().equals(IStrategy.class.getName())
                        && IStrategy.class.isAssignableFrom(clazz)) {
                    interfaceClass = clazz;
                    break;
                }
            }
            if (interfaceClass == null) {
                continue;
            }

            getExtensionClass.apply(interfaceClass).cache(beanName, beanClass);
            getExtensionInstances.apply(interfaceClass).cache(beanName, bean);
        }
    }
}
