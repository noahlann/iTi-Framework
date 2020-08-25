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

import java.util.regex.Pattern;

/**
 * @author NorthLan
 * @date 2020-08-22
 * @url https://noahlan.com
 */
public interface PatternPool {
    /**
     * Base64
     * 1. 字符串只可能包含A-Z，a-z，0-9，+，/，=字符
     * 2. 字符串长度是4的倍数
     * 3. =只会出现在字符串最后，可能没有或者一个等号或者两个等号
     */
    Pattern PATTERN_BASE64 = Pattern.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)?$");
}
