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

package org.lan.iti.iha.security.processor;

import org.lan.iti.common.extension.IExtension;
import org.lan.iti.common.extension.annotation.Extension;
import org.lan.iti.iha.security.authentication.Authentication;
import org.lan.iti.iha.security.exception.authentication.AuthenticationException;
import org.lan.iti.iha.security.mgt.RequestParameter;

/**
 * 认证处理器
 *
 * @author NorthLan
 * @date 2021/8/3
 * @url https://blog.noahlan.com
 */
@Extension
public interface AuthenticationProcessor extends IExtension<String> {

    /**
     * 决定该流程处理器是否要处理的方法
     *
     * @param parameter      请求参数
     * @param authentication 认证对象
     * @return true处理 false不处理
     */
    boolean support(RequestParameter parameter,
                    Authentication authentication) throws AuthenticationException;

    /**
     * 处理流程
     *
     * @param parameter      请求参数
     * @param authentication 认证对象
     * @param chain          处理链
     * @return 认证对象
     */
    Authentication process(RequestParameter parameter,
                           Authentication authentication,
                           ProcessChain chain) throws AuthenticationException;
}
