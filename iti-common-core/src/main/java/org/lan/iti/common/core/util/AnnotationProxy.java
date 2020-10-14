/*
 *
 *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.common.core.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * 注解动态代理
 *
 * @author NorthLan
 * @date 2020-09-13
 * @url https://noahlan.com
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
public class AnnotationProxy implements Annotation, InvocationHandler {

    @Getter
    private final Class<? extends Annotation> annotationType;
    private final Map<String, Object> values;

    @SuppressWarnings({"unchecked", "serial", "rawtypes"})
    public static <T extends Annotation> T of(Class<T> annotation, Map<String, Object> values) {
        return (T) Proxy.newProxyInstance(annotation.getClassLoader(),
                new Class[]{annotation},
                new AnnotationProxy(annotation, new HashMap(values) {{
                    // Required because getDefaultValue() returns null for this call
                    put("annotationType", annotation);
                }}));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return values.getOrDefault(method.getName(), method.getDefaultValue());
    }
}
