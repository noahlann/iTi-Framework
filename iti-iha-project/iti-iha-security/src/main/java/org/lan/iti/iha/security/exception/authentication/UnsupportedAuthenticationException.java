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

package org.lan.iti.iha.security.exception.authentication;

/**
 * Authentication exception: Can not support this request
 * SubjectCreator list unable to create the corresponding subject according to the request
 * <p>
 * 认证异常：无法支持此请求
 * SubjectCreator列表 无法根据请求创建对应的Subject
 *
 * @author NorthLan
 * @date 2021/7/28
 * @url https://blog.noahlan.com
 */
public class UnsupportedAuthenticationException extends AuthenticationException {
    private static final long serialVersionUID = 7623198053160685909L;
    private static UnsupportedAuthenticationException instance;

    public UnsupportedAuthenticationException(String message) {
        super(message);
    }

    public UnsupportedAuthenticationException(String error, String message) {
        super(error, message);
    }

    public static UnsupportedAuthenticationException getDefaultInstance() {
        if (instance == null) {
            instance = new UnsupportedAuthenticationException("ProcessorManager was required.");
        }
        return instance;
    }
}
