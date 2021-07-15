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

package org.lan.iti.common.core.exception;

/**
 * 顶层异常规范
 *
 * @author NorthLan
 * @date 2021-03-02
 * @url https://noahlan.com
 */
public interface IExceptionSpec {

    /**
     * 获取错误编码
     *
     * @return 规范的错误编码
     */
    Integer getCode();

    /**
     * 获取错误消息
     * TODO i18n
     *
     * @return 错误信息
     */
    String getMessage();
}
