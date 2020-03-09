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

package org.lan.iti.common.security.enums;

/**
 * 鉴权 类型接口
 *
 * @author NorthLan
 * @date 2020-03-02
 * @url https://noahlan.com
 */
interface IAuthType {
    /**
     * 获取类型代码
     */
    String getType();

    /**
     * 获取类型描述
     */
    String getDescription();
}
