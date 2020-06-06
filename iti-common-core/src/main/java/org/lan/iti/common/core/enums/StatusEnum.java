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

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 状态 枚举
 * <pre>
 *     通常与数据库值对应
 * </pre>
 *
 * @author NorthLan
 * @date 2020-02-20
 * @url https://noahlan.com
 */
@Getter
@AllArgsConstructor
public enum StatusEnum {
    // status flag
    NORMAL(1, "正常"),
    LOCK(9, "锁定");

    private Integer value;
    private String desc;
}
