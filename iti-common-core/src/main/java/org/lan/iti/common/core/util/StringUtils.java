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

package org.lan.iti.common.core.util;

import lombok.experimental.UtilityClass;

/**
 * 字符串 工具类
 *
 * @author NorthLan
 * @date 2020-02-24
 * @url https://noahlan.com
 */
@UtilityClass
public class StringUtils {

    /**
     * 判断字符串是否为null | "null" | "undefined"
     *
     * @param str 待输入字符串
     */
    public boolean isNullOrUndefined(String str) {
        return str == null || "null".equalsIgnoreCase(str) || "undefined".equalsIgnoreCase(str);
    }
}
