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

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import org.lan.iti.common.core.interfaces.IFunction;
import org.springframework.lang.Nullable;

import java.util.Map;

/**
 * Java 8 lambda 工具类
 *
 * @author NorthLan
 * @date 2020-04-28
 * @url https://noahlan.com
 */
@UtilityClass
public class LambdaUtils {
    private final Map<String, String> CACHE_FIELD_MAP = MapUtil.newConcurrentHashMap();
    private final Map<String, String> CACHE_CAMEL_FIELD_MAP = MapUtil.newConcurrentHashMap();

    /**
     * 获取Field名称
     *
     * @param getter getter
     * @param <T>    入参类型
     * @param <R>    出参类型
     * @return 变量名
     */
    @Nullable
    public <T, R> String getFieldName(IFunction<T, R> getter) {
        String simpleClassName = getter.getClass().getSimpleName();
        if (CACHE_FIELD_MAP.containsKey(simpleClassName)) {
            return CACHE_FIELD_MAP.get(simpleClassName);
        }
        String result = getter.getImplFieldName();
        if (result == null) {
            return null;
        }
        return CACHE_FIELD_MAP.put(simpleClassName, result);
    }

    /**
     * 获取Field名称
     * <p>
     * 转下划线形式
     * </p>
     *
     * @param getter getter
     * @param <T>    入参类型
     * @param <R>    出参类型
     * @return 变量名
     */
    @Nullable
    public <T, R> String getFieldNameCamel(IFunction<T, R> getter) {
        String simpleClassName = getter.getClass().getSimpleName();
        if (CACHE_CAMEL_FIELD_MAP.containsKey(simpleClassName)) {
            return CACHE_CAMEL_FIELD_MAP.get(simpleClassName);
        }
        String result = StrUtil.toUnderlineCase(getFieldName(getter));
        if (result == null) {
            return null;
        }
        return CACHE_CAMEL_FIELD_MAP.put(simpleClassName, result);
    }
}
