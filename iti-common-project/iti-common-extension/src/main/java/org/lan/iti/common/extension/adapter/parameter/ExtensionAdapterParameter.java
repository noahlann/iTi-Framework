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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.lan.iti.common.extension.ExtensionConstants;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author NorthLan
 * @date 2021-07-09
 * @url https://noahlan.com
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class ExtensionAdapterParameter {
    private final Map<String, Object> map;

    public static ParameterBuilder builder() {
        return new ParameterBuilder();
    }

    public ExtensionAdapterParameter merge(ExtensionAdapterParameter parameter) {
        for (Map.Entry<String, Object> entry : parameter.map.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public <T> T getByKey(String key) {
        return getByKey(key, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T getByKey(String key, T defaultValue) {
        return getByKey(key, defaultValue, obj -> (T) obj);
    }

    public <T> T getByKey(String key, T defaultValue, Function<Object, T> cast) {
        Object obj = map.get(key);
        if (obj == null) {
            return defaultValue;
        }
        return cast.apply(map.get(key));
    }

    public ClassLoader getClassLoader() {
        return getByKey(ExtensionConstants.KEY_CLASS_LOADER);
    }

    public List<String> getPackageNames() {
        List<String> lists = getByKey(ExtensionConstants.KEY_PACKAGE_NAMES);
        return lists.stream().distinct().collect(Collectors.toList());
    }

    public Class<?> getInterfaceClass() {
        return getByKey(ExtensionConstants.KEY_INTERFACE_CLASS);
    }

    public Boolean isCheckInterfaceClass() {
        return getByKey(ExtensionConstants.KEY_CHECK_INTERFACE_CLASS, false);
    }

    public Class<?> getType() {
        return getByKey(ExtensionConstants.KEY_TYPE);
    }

    public Set<Class<?>> getClasses() {
        return getByKey(ExtensionConstants.KEY_CLASSES);
    }
}
