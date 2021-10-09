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

package org.lan.iti.common.core.util;

import cn.hutool.core.util.ArrayUtil;
import lombok.experimental.UtilityClass;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author NorthLan
 * @date 2021/9/30
 * @url https://blog.noahlan.com
 */
@UtilityClass
public class MapUtil extends cn.hutool.core.map.MapUtil {

    /**
     * 扁平化Map，附带过滤器，可忽略顶层key
     *
     * @param map        待扁平化Map
     * @param ignoreKeys 忽略key列表
     * @param <V>        值类型
     * @return Map
     */
    public <V> Map<String, V> flatten(Map<String, V> map, String... ignoreKeys) {
        return flatten(map, s -> {
            if (ignoreKeys.length <= 0) {
                return true;
            }
            return !ArrayUtil.contains(ignoreKeys, s);
        });
    }

    /**
     * 扁平化Map，附带过滤器，可通过predicate过滤不需要扁平化的keys
     *
     * @param map             待扁平化Map
     * @param filterPredicate 过滤器谓语
     * @param <K>             键
     * @param <V>             值
     * @return Map
     */
    public <K, V> Map<K, V> flatten(Map<K, V> map, Predicate<K> filterPredicate) {
        return flattenStream(map, filterPredicate)
                .collect(HashMap::new, (m, it) -> m.put(it.getKey(), it.getValue()), HashMap::putAll);
    }

    /**
     * 扁平化Map，附带过滤器，可通过predicate过滤不需要扁平化的keys
     *
     * @param map             待扁平化Map
     * @param filterPredicate 过滤器谓语
     * @param <K>             键
     * @param <V>             值
     * @return Entry流
     */
    public <K, V> Stream<Map.Entry<K, V>> flattenStream(Map<K, V> map, Predicate<K> filterPredicate) {
        return map.entrySet()
                .stream()
                .filter(p -> filterPredicate.test(p.getKey()))
                .flatMap(MapUtil::extractValue);
    }

    @SuppressWarnings("unchecked")
    private <K, V> Stream<Map.Entry<K, V>> extractValue(Map.Entry<K, V> entry) {
        if (entry.getValue() instanceof Map) {
            return flattenStream((Map) entry.getValue(), null);
        } else {
            return Stream.of(new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue()));
        }
    }
}
