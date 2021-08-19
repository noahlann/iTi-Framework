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

package org.lan.iti.common.core.support;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 框架统一Enum顶层接口
 * <p>可实现统一的参数转换流程</p>
 * <p>统一化code</p>
 *
 * @author NorthLan
 * @date 2021-03-22
 * @url https://noahlan.com
 */
public interface IEnum<C> {
    /**
     * 唯一编码
     * <p>不提供setter，由子类自行处理</p>
     * <p>标记@JsonValue，使用code而不是name()返回</p>
     *
     * @return 返回唯一标识
     */
    @JsonValue
    C getCode();

    /**
     * 错误信息
     */
    String getMessage();
}
