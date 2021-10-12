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

package org.lan.iti.cloud.jackson.dynamicfilter.resolver;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import lombok.AllArgsConstructor;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * ITIFilter 底层实现
 * <p>
 * {@link org.lan.iti.cloud.jackson.dynamicfilter.annotation.ITIFilter}
 * <br/>
 * {@link org.lan.iti.cloud.jackson.dynamicfilter.annotation.ITIFilters}
 *
 * @author NorthLan
 * @date 2021/10/11
 * @url https://blog.noahlan.com
 */
public abstract class AbstractITIFilterResolver<A extends Annotation> extends DynamicFilterResolver<A> {

    @Override
    public PropertyFilter apply(A annotation) {
        return new SimpleBeanPropertyFilter() {
            private final FilterProps filterProps = buildProps(annotation);

            @Override
            public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
                if (isFilter(filterProps, annotation, pojo.getClass(), writer.getName())) {
                    writer.serializeAsField(pojo, jgen, provider);
                } else if (!jgen.canOmitFields()) {
                    writer.serializeAsOmittedField(pojo, jgen, provider);
                }
            }
        };
    }

    private boolean isFilter(FilterProps props, A annotation, Class<?> clazz, String name) {
        Set<String> includeFields = new HashSet<>();
        for (Map.Entry<Class<?>, Set<String>> entry : props.includeMap.entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) {
                includeFields.addAll(entry.getValue());
            }
        }
        Set<String> excludeFields = new HashSet<>();
        for (Map.Entry<Class<?>, Set<String>> entry : props.excludeMap.entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) {
                excludeFields.addAll(entry.getValue());
            }
        }

        if (!CollUtil.contains(excludeFields, name)) {
            return true;
        }
        return CollUtil.contains(includeFields, name);
    }

    protected abstract FilterProps buildProps(A annotation);

    @AllArgsConstructor
    public static class FilterProps {
        private Map<Class<?>, Set<String>> includeMap;
        private Map<Class<?>, Set<String>> excludeMap;
    }
}
