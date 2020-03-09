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

package org.lan.iti.common.core.exception.enums;

/**
 * 异常枚举统一接口
 *
 * @author NorthLan
 * @date 2020-02-22
 * @url https://noahlan.com
 */
public interface IExceptionEnum {
    /**
     * 异常状态码
     */
    Integer getCode();

    /**
     * 异常信息
     */
    String getMessage();
}
