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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author NorthLan
 * @date 2021/9/30
 * @url https://blog.noahlan.com
 */
@UtilityClass
public class MapUtil extends cn.hutool.core.map.MapUtil {

    /**
     * 扁平化Map，通过指定key进行扁平化
     *
     * @param map  待扁平化Map
     * @param keys key列表
     * @param <V>  值类型
     * @return Map
     */
    public <V> Map<String, V> flattenByKeys(Map<String, V> map, String... keys) {
        return flattenPredicate(map, s -> {
            if (keys.length <= 0) {
                return false;
            }
            return ArrayUtil.contains(keys, s.getKey());
        });
    }

    /**
     * 扁平化Map，附带过滤器，可忽略顶层key
     *
     * @param map        待扁平化Map
     * @param ignoreKeys 忽略key列表
     * @param <V>        值类型
     * @return Map
     */
    public <V> Map<String, V> flatten(Map<String, V> map, String... ignoreKeys) {
        return flattenPredicate(map, s -> {
            if (ignoreKeys.length <= 0) {
                return true;
            }
            return !ArrayUtil.contains(ignoreKeys, s.getKey());
        });
    }

    /**
     * 扁平化Map，附带过滤器，可通过predicate过滤不需要扁平化的keys
     *
     * @param map       待扁平化Map
     * @param predicate 断言 返回true表示该元素会被扁平化
     * @param <K>       键
     * @param <V>       值
     * @return Map
     */
    public <K, V> Map<K, V> flattenPredicate(Map<K, V> map, Predicate<Map.Entry<K, V>> predicate) {
        Map<K, V> result = new HashMap<>();

        // clone map 避免影响原map
        Map<K, V> tmp = new HashMap<>(map);
        Iterator<Map.Entry<K, V>> iter = tmp.entrySet().iterator();

        Map<K, V> extractMap = new HashMap<>();
        while (iter.hasNext()) {
            Map.Entry<K, V> entry = iter.next();
            if (predicate == null || !predicate.test(entry)) {
                result.put(entry.getKey(), entry.getValue());
            } else {
                iter.remove();
                Map<K, V> tmpExtractMap = extractValue(entry.getValue());
                if (tmpExtractMap == null) {
                    result.put(entry.getKey(), entry.getValue());
                    continue;
                }
                extractMap.putAll(tmpExtractMap);
            }
        }
        result.putAll(extractMap);

        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <K, V> Map<K, V> extractValue(V value) {
        if (value instanceof Map) {
            return flattenPredicate((Map) value, null);
        } else {
            return null;
        }
    }
}
