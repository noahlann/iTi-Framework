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

package org.lan.iti.common.extension.adapter.parameter;

import org.lan.iti.common.core.util.ClassLoaderUtil;
import org.lan.iti.common.extension.ExtensionConstants;
import org.lan.iti.common.extension.ExtensionLoader;

import java.util.*;
import java.util.function.BiFunction;

/**
 * @author NorthLan
 * @date 2021-07-10
 * @url https://noahlan.com
 */
public class ParameterBuilder implements IParameterBuilder {
    private final Map<String, Object> map = new HashMap<>();

    public ReflectionBuilder reflect() {
        return new ReflectionBuilder(this, this.map);
    }

    public TypeClassBuilder typeClassBuilder() {
        return new TypeClassBuilder(this, this.map);
    }

    public <T extends AbstractParameterBuilder> T custom(BiFunction<ParameterBuilder, Map<String, Object>, T> function) {
        return function.apply(this, map);
    }

    @Override
    public ExtensionAdapterParameter build() {
        return new ExtensionAdapterParameter(this.map);
    }


    public static final class ReflectionBuilder extends AbstractParameterBuilder {
        protected ReflectionBuilder(ParameterBuilder builder, Map<String, Object> map) {
            super(builder, map);
        }

        public ReflectionBuilder classLoader(ClassLoader classLoader) {
            ClassLoader classLoaderToUse = classLoader;
            if (classLoaderToUse == null) {
                classLoaderToUse = ClassLoaderUtil.getClassLoader(ExtensionLoader.class);
            }
            map.put(ExtensionConstants.KEY_CLASS_LOADER, classLoaderToUse);
            return this;
        }

        public ReflectionBuilder interfaceClass(Class<?> clazz) {
            if (!clazz.isInterface()) {
                throw new IllegalArgumentException("interfaceClass must be interface class");
            }
            map.put(ExtensionConstants.KEY_INTERFACE_CLASS, clazz);
            return this;
        }

        public ReflectionBuilder checkInterfaceClass(boolean check) {
            map.put(ExtensionConstants.KEY_CHECK_INTERFACE_CLASS, check);
            return this;
        }

        @SuppressWarnings("unchecked")
        public ReflectionBuilder packageNames(String... packageNames) {
            List<String> lists = (List<String>) map.computeIfAbsent(ExtensionConstants.KEY_PACKAGE_NAMES, it -> new ArrayList<>());
            lists.addAll(Arrays.asList(packageNames));
            return this;
        }

        public ReflectionBuilder packageName(String packageName) {
            return packageNames(packageName);
        }
    }

    public static final class TypeClassBuilder extends AbstractParameterBuilder {
        protected TypeClassBuilder(ParameterBuilder builder, Map<String, Object> map) {
            super(builder, map);
        }

        @SuppressWarnings("unchecked")
        private Set<Class<?>> computeIfAbsentClasses() {
            return (Set<Class<?>>) map.computeIfAbsent(ExtensionConstants.KEY_CLASSES, it -> new HashSet<Class<?>>());
        }

        public TypeClassBuilder type(Class<?> type) {
            map.put(ExtensionConstants.KEY_TYPE, type);
            return this;
        }

        public TypeClassBuilder classes(Set<Class<?>> classes) {
            computeIfAbsentClasses().addAll(classes);
            return this;
        }

        public TypeClassBuilder classes(Class<?>... classes) {
            computeIfAbsentClasses().addAll(Arrays.asList(classes));
            return this;
        }
    }
}