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

package org.lan.iti.common.extension.adapter;

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.extension.ExtensionFactory;
import org.lan.iti.common.extension.IExtension;
import org.lan.iti.common.extension.adapter.parameter.ExtensionAdapterParameter;
import org.lan.iti.common.extension.support.NamedClassCache;

import java.util.Set;
import java.util.function.Function;

/**
 * @author NorthLan
 * @date 2021-07-09
 * @url https://noahlan.com
 */
@Slf4j
public class TypeClassExtensionAdapter extends AbstractExtensionAdapter {
    private Class<?> type;
    private Set<Class<?>> classes;

    public TypeClassExtensionAdapter(Function<Class<?>, NamedClassCache<Class<?>>> getExtensionClasses, Function<Class<?>, NamedClassCache<Object>> getExtensionInstances) {
        super(getExtensionClasses, getExtensionInstances);
    }

    @Override
    public boolean matches(Object params) {
        return type != null && classes != null;
    }

    @Override
    public void loadParameter(ExtensionAdapterParameter param) {
        this.type = param.getType();
        this.classes = param.getClasses();
    }

    @Override
    protected void reset() {
        this.type = null;
        this.classes = null;
    }

    @Override
    public void init() {
        log.debug("{} init...", this.getClass().getSimpleName());
        if (type == null || classes == null) {
            return;
        }
        if (type.equals(ExtensionFactory.class)) {
            return;
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type (" + type + ") is not an interface!");
        }
        if (!IExtension.class.isAssignableFrom(type)) {
            throw new IllegalArgumentException("Extension type (" + type + ") must implements " + IExtension.class.getName());
        }
//        if (!ExtensionUtil.withAnnotation(type)) {
//            throw new IllegalArgumentException("Extension type (" + type +
//                    ") is not an extension, because it is NOT annotated with @" + Extension.class.getSimpleName() + "!");
//        }
        // TODO check classes if subType of type
        // add all given classes
        for (Class<?> clazz : classes) {
            getExtensionClass.apply(type).cache(clazz.getSimpleName(), clazz);
        }
    }
}
