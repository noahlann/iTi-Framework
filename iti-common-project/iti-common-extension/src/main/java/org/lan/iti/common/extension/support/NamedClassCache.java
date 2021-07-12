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

package org.lan.iti.common.extension.support;

import cn.hutool.core.map.MapUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author NorthLan
 * @date 2021-07-10
 * @url https://noahlan.com
 */
public class NamedClassCache<T> {
    private final ConcurrentMap<String, T> namedMap = new ConcurrentHashMap<>();

    public Set<T> getAll() {
        return new HashSet<>(namedMap.values());
    }

    public void cache(Map<String, T> map) {
        for (Map.Entry<String, T> entry : map.entrySet()) {
            namedMap.put(entry.getKey(), entry.getValue());
        }
    }

    public void cache(String name, T obj) {
        if (obj != null) {
            namedMap.put(name, obj);
        }
    }

    public T getByName(String name) {
        return namedMap.get(name);
    }

    public ConcurrentMap<String, T> getMap() {
        return namedMap;
    }

    public T getByValueClass(Class<?> clazz) {
        for (Map.Entry<String, T> entry : namedMap.entrySet()) {
            if (clazz.equals(entry.getValue().getClass())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return MapUtil.isEmpty(namedMap);
    }
}
