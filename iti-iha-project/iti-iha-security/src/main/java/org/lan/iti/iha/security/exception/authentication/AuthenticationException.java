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

import org.lan.iti.common.core.support.IEnum;
import org.lan.iti.iha.security.exception.SecurityException;

/**
 * Authentication exception
 * Basic exceptions, exceptions related to custom authentication need to inherit
 * <p>
 * 认证相关异常
 * 自定义认证异常需要继承此类
 *
 * @author NorthLan
 * @date 2021/7/28
 * @url https://blog.noahlan.com
 */
public class AuthenticationException extends SecurityException {
    private static final long serialVersionUID = -38845565736355068L;

    public AuthenticationException(IEnum<String> spec) {
        super(spec);
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String errorCode, String message) {
        super(errorCode, message);
    }
}
