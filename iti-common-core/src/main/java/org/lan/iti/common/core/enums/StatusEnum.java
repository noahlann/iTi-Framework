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

package org.lan.iti.common.core.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 状态 枚举
 *
 * @author NorthLan
 * @date 2020-02-20
 * @url https://noahlan.com
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum StatusEnum {
    // 启用禁用
    ENABLED(1, "启用"),
    DISABLED(0, "禁用"),
    // return status
    SUCCESS(1, "成功"),
    FAIL(0, "失败"),
    // status flag
    DELETE(0, "删除"),
    NORMAL(1, "正常"),
    LOCK(9, "锁定"),
    // logicDelete
    LOGIC_DELETE(1, "逻辑删除"),
    LOGIC_NOT_DELETE(0, "逻辑未删除");

    private Integer code;
    private String desc;
}
