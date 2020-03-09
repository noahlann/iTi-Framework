/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.common.jackson.dynamicfilter.resolver;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import lombok.AllArgsConstructor;
import org.lan.iti.common.jackson.dynamicfilter.annotation.ITIFilters;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * ITIFilter 默认实现
 *
 * @author NorthLan
 * @date 2020-03-05
 * @url https://noahlan.com
 */
public class ITIFilterResolver extends DynamicFilterResolver<ITIFilters> {
    @Override
    public PropertyFilter apply(ITIFilters itiFilters) {
        return new SimpleBeanPropertyFilter() {
            private FilterProps filterProps = buildProps(itiFilters);

            @Override
            public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
                if (isFilter(filterProps, pojo.getClass(), writer.getName())) {
                    writer.serializeAsField(pojo, jgen, provider);
                } else if (!jgen.canOmitFields()) {
                    writer.serializeAsOmittedField(pojo, jgen, provider);
                } else {
                    super.serializeAsField(pojo, jgen, provider, writer);
                }
            }
        };
    }

    private boolean isFilter(FilterProps props, Class<?> clazz, String name) {
        Set<String> includeFields = props.includeMap.getOrDefault(clazz, Collections.emptySet());
        Set<String> excludeFields = props.excludeMap.getOrDefault(clazz, Collections.emptySet());
        if (!CollectionUtils.isEmpty(excludeFields)) {
            return !excludeFields.contains(name);
        }
        if (!CollectionUtils.isEmpty(includeFields)) {
            return includeFields.contains(name);
        }
        return true;
    }

    private FilterProps buildProps(ITIFilters filters) {
        Map<Class<?>, Set<String>> includeMap = new HashMap<>();
        Map<Class<?>, Set<String>> excludeMap = new HashMap<>();
        Arrays.stream(filters.value()).forEach(it -> {
            includeMap.put(it.type(), new LinkedHashSet<>(Arrays.asList(it.includes())));
            excludeMap.put(it.type(), new LinkedHashSet<>(Arrays.asList(it.excludes())));
        });
        return new FilterProps(includeMap, excludeMap);
    }

    @AllArgsConstructor
    public static class FilterProps {
        private Map<Class<?>, Set<String>> includeMap;
        private Map<Class<?>, Set<String>> excludeMap;
    }
}
