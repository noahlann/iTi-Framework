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

package org.lan.iti.cloud.security.config.http;


import javax.servlet.http.HttpSession;

/**
 * 指定 iTi Security 的各种会话创建策略。
 *
 * @author NorthLan
 * @date 2021/10/15
 * @url https://blog.noahlan.com
 */
public enum SessionCreationPolicy {
    /**
     * Always create an {@link HttpSession}
     */
    ALWAYS,

    /**
     * Spring Security will never create an {@link HttpSession}, but will use the
     * {@link HttpSession} if it already exists
     */
    NEVER,

    /**
     * Spring Security will only create an {@link HttpSession} if required
     */
    IF_REQUIRED,

    /**
     * Spring Security will never create an {@link HttpSession} and it will never use it
     * to obtain the {@link org.lan.iti.iha.security.context.SecurityContext}
     */
    STATELESS
}
