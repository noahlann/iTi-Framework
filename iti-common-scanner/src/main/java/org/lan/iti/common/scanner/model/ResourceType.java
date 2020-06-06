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

package org.lan.iti.common.scanner.model;

/**
 * 资源类型
 *
 * @author NorthLan
 * @date 2020-06-06
 * @url https://noahlan.com
 */
public interface ResourceType {
    /**
     * 系统资源
     * <pre>
     *     仅特殊权限用户可操作
     * </pre>
     */
    String SYSTEM = "system";

    /**
     * 默认资源类型,注解未指定时使用
     */
    String DEFAULT = "default";
}
