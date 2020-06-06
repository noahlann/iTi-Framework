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

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回状态 枚举
 *
 * @author NorthLan
 * @date 2020-06-06
 * @url https://noahlan.com
 */
@Getter
@AllArgsConstructor
public enum ReturnStatusEnum {
    // return status
    SUCCESS(1, "成功"),
    FAIL(0, "失败");

    private Integer value;
    private String desc;
}
