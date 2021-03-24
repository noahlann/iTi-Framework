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

package org.lan.iti.cloud.feign;

import feign.*;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.core.convert.ConversionService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static feign.Util.checkState;
import static feign.Util.emptyToNull;

/**
 * 扩展 SpringMvcContract 解析器
 * <p>
 * 实现能够同时解析SpringMvc 与 Feign 自带注解
 * 目前实现:
 * Header
 * Param
 * QueryMap
 * HeaderMap
 * </p>
 *
 * @author NorthLan
 * @date 2020-04-15
 * @url https://noahlan.com
 */
public class ITIFeignContract extends SpringMvcContract {

    public ITIFeignContract(List<AnnotatedParameterProcessor> annotatedParameterProcessors, ConversionService conversionService) {
        super(annotatedParameterProcessors, conversionService);
    }

    @Override
    protected void processAnnotationOnMethod(MethodMetadata data, Annotation methodAnnotation, Method method) {
        super.processAnnotationOnMethod(data, methodAnnotation, method);
        // feign 仅解析 headers
        Class<? extends Annotation> annotationType = methodAnnotation.annotationType();
        if (annotationType == Headers.class) {
            String[] headersOnMethod = ((Headers) methodAnnotation).value();
            checkState(headersOnMethod.length > 0, "Headers annotation was empty on method %s.",
                    method.getName());
            data.template().headers(toMap(headersOnMethod));
        }
    }

    @Override
    protected boolean processAnnotationsOnParameter(MethodMetadata data, Annotation[] annotations, int paramIndex) {
        boolean isMvcHttpAnnotation = super.processAnnotationsOnParameter(data, annotations, paramIndex);
        boolean isFeignHttpAnnotation = false;

        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (annotationType == Param.class) {
                Param paramAnnotation = (Param) annotation;
                String name = paramAnnotation.value();
                checkState(emptyToNull(name) != null, "Param annotation was empty on param %s.",
                        paramIndex);
                nameParam(data, name, paramIndex);
                Class<? extends Param.Expander> expander = paramAnnotation.expander();
                if (expander != Param.ToStringExpander.class) {
                    data.indexToExpanderClass().put(paramIndex, expander);
                }
                data.indexToEncoded().put(paramIndex, paramAnnotation.encoded());
                isFeignHttpAnnotation = true;
                if (!data.template().hasRequestVariable(name)) {
                    data.formParams().add(name);
                }
            } else if (annotationType == QueryMap.class) {
                checkState(data.queryMapIndex() == null,
                        "QueryMap annotation was present on multiple parameters.");
                data.queryMapIndex(paramIndex);
                data.queryMapEncoded(((QueryMap) annotation).encoded());
                isFeignHttpAnnotation = true;
            } else if (annotationType == HeaderMap.class) {
                checkState(data.headerMapIndex() == null,
                        "HeaderMap annotation was present on multiple parameters.");
                data.headerMapIndex(paramIndex);
                isFeignHttpAnnotation = true;
            }
        }

        return isMvcHttpAnnotation || isFeignHttpAnnotation;
    }

    private static Map<String, Collection<String>> toMap(String[] input) {
        Map<String, Collection<String>> result =
                new LinkedHashMap<String, Collection<String>>(input.length);
        for (String header : input) {
            int colon = header.indexOf(':');
            String name = header.substring(0, colon);
            if (!result.containsKey(name)) {
                result.put(name, new ArrayList<String>(1));
            }
            result.get(name).add(header.substring(colon + 1).trim());
        }
        return result;
    }
}
