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

package org.lan.iti.common.core.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.lan.iti.common.core.interfaces.Value;

/**
 * 错误级别枚举
 *
 * @author NorthLan
 * @date 2020-05-14
 * @url https://noahlan.com
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ErrorLevelEnum implements Value<Integer> {
    FATAL(1), // 致命级，可能影响整个系统
    IMPORTANT(2), // 严重错误，可能影响整个服务
    PRIMARY(3), // 主要错误，可能影响整个业务
    NORMAL(4), // 普通错误，对业务流程上不造成重大影响
    IGNORE(5) // 可忽略错误，仅仅作为错误提示，没有任何影响
    ;
    private int value;

    @Override
    public Integer getValue() {
        return this.value;
    }
}
