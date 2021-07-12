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

package org.lan.iti.cloud.extension;

import org.lan.iti.common.extension.IExtension;
import org.lan.iti.common.extension.adapter.AbstractExtensionAdapter;
import org.lan.iti.common.extension.adapter.parameter.ExtensionAdapterParameter;
import org.lan.iti.common.extension.support.NamedClassCache;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author NorthLan
 * @date 2021-07-11
 * @url https://noahlan.com
 */
public class SpringExtensionAdapter extends AbstractExtensionAdapter {
    private ApplicationContext applicationContext;
    // TODO merge
    private Map<String, Object> beans;

    public SpringExtensionAdapter(Function<Class<?>, NamedClassCache<Class<?>>> getExtensionClasses, Function<Class<?>, NamedClassCache<Object>> getExtensionInstances) {
        super(getExtensionClasses, getExtensionInstances);
    }

    @Override
    public boolean matches(Object params) {
        return this.applicationContext != null;
    }

    @Override
    protected void loadParameter(ExtensionAdapterParameter parameter) {
        this.applicationContext = parameter.getByKey(SpringExtensionConstants.KEY_APPLICATION_CONTEXT);
        this.beans = parameter.getByKey(SpringExtensionConstants.KEY_BEANS, new HashMap<>());
    }

    @Override
    public void init() {
        // TODO 优化启动过程
        Map<String, IExtension> beans = applicationContext.getBeansOfType(IExtension.class);
        for (Map.Entry<String, IExtension> entry : beans.entrySet()) {
            String beanName = entry.getKey();
            Object bean = entry.getValue();
            Class<?> beanClass = bean.getClass();
            Class<?> interfaceClass = null;

            for (Class<?> clazz : beanClass.getInterfaces()) {
                if (!clazz.getName().equals(IExtension.class.getName())
                        && IExtension.class.isAssignableFrom(clazz)) {
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
