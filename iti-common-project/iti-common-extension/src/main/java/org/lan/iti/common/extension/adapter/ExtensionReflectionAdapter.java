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

import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.util.StringUtil;
import org.lan.iti.common.extension.ExtensionFactory;
import org.lan.iti.common.extension.IExtension;
import org.lan.iti.common.extension.adapter.parameter.ExtensionAdapterParameter;
import org.lan.iti.common.extension.annotation.Extension;
import org.lan.iti.common.extension.support.NamedClassCache;
import org.lan.iti.common.extension.util.ExtensionUtil;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * Reflection Extension Init Adapter
 * <p>
 * using {@link org.reflections.Reflections} utils
 *
 * @author NorthLan
 * @date 2021-07-09
 * @url https://noahlan.com
 */
@Slf4j
public class ExtensionReflectionAdapter extends AbstractExtensionAdapter {
    private ClassLoader classLoader;
    private Class<?> interfaceClass;
    private boolean checkInterfaceClass = false;
    private String[] packageNames;

    public ExtensionReflectionAdapter(Function<Class<?>, NamedClassCache<Class<?>>> getExtensionClasses, Function<Class<?>, NamedClassCache<Object>> getExtensionInstances) {
        super(getExtensionClasses, getExtensionInstances);
    }

    @Override
    public boolean matches(Object params) {
        return ArrayUtil.isNotEmpty(this.packageNames);
    }

    @Override
    public void loadParameter(ExtensionAdapterParameter param) {
        this.classLoader = param.getClassLoader();
        this.packageNames = ArrayUtil.toArray(param.getPackageNames(), String.class);
        this.interfaceClass = param.getInterfaceClass();
        this.checkInterfaceClass = param.isCheckInterfaceClass();
    }

    @Override
    protected void reset() {
        this.packageNames = null;
        this.classLoader = null;
        this.checkInterfaceClass = false;
        this.interfaceClass = null;
    }

    @Override
    public void init() {
        if (log.isDebugEnabled()) {
            log.debug("load extension by reflections.");
        }
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackages(packageNames)
                .addClassLoader(classLoader)
                .setExpandSuperTypes(false)
                .addScanners(new TypeAnnotationsScanner(), new SubTypesScanner(false)));
        Set<Class<?>> typesClass = reflections.getTypesAnnotatedWith(Extension.class, false);
        Set<Class<?>> interfaces = new HashSet<>();
        Set<Class<?>> classes = new HashSet<>();
        for (Class<?> typeClass : typesClass) {
            if (typeClass.equals(ExtensionFactory.class)) {
                continue;
            }
            if (ExtensionAdapter.class.isAssignableFrom(typeClass)) {
                continue;
            }
            if (this.checkInterfaceClass && this.interfaceClass != null
                    && !interfaceClass.isAssignableFrom(typeClass)) {
                log.error("{} 需要继承 {}", typeClass.getName(), IExtension.class.getName());
                continue;
            }
            if (ExtensionUtil.isRealAbstract(typeClass)) {
                continue;
            }
            if (typeClass.isInterface()) {
                if (ExtensionUtil.withAnnotation(typeClass, Extension.class)) {
                    interfaces.add(typeClass);
                }
            } else {
                classes.add(typeClass);
            }
        }
        // flatmap
        for (Class<?> interfaceClass : interfaces) {
            for (Class<?> instanceClass : classes) {
                if (Arrays.stream(instanceClass.getInterfaces()).anyMatch(it -> it.getName().equals(interfaceClass.getName()))) {
                    getExtensionClass.apply(interfaceClass).cache(StringUtil.lowerFirst(instanceClass.getSimpleName()), instanceClass);
                }
            }
        }
    }
}
