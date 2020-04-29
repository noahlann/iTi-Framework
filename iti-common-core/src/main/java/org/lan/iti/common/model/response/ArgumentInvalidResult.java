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

package org.lan.iti.common.model.response;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 参数错误 数据模型
 *
 * @author NorthLan
 * @date 2020-04-29
 * @url https://noahlan.com
 */
@ApiModel("参数错误数据模型")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArgumentInvalidResult {
    /**
     * 错误信息
     */
    private String defaultMessage;

    /**
     * 错误对象
     */
    private String object;

    /**
     * 错误字段
     */
    private String field;

    /**
     * 被拒绝的值
     */
    private Object rejectValue;
}
