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

package org.lan.iti.cloud.jackson.dynamicfilter.resolver;

import org.lan.iti.cloud.jackson.dynamicfilter.annotation.ITIFilters;

import java.util.*;

/**
 * ITIFilters 默认实现
 *
 * @author NorthLan
 * @date 2020-03-05
 * @url https://noahlan.com
 */
public class ITIFiltersResolver extends AbstractITIFilterResolver<ITIFilters> {

    @Override
    protected FilterProps buildProps(ITIFilters filters) {
        Map<Class<?>, Set<String>> includeMap = new HashMap<>();
        Map<Class<?>, Set<String>> excludeMap = new HashMap<>();
        Arrays.stream(filters.value()).forEach(it -> {
            includeMap.put(it.type(), new LinkedHashSet<>(Arrays.asList(it.includes())));
            excludeMap.put(it.type(), new LinkedHashSet<>(Arrays.asList(it.excludes())));
        });
        return new FilterProps(includeMap, excludeMap);
    }

}
