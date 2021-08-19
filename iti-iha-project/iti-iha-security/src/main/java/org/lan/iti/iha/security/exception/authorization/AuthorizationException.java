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

package org.lan.iti.iha.security.exception.authorization;

import org.lan.iti.common.core.support.IEnum;
import org.lan.iti.iha.security.exception.SecurityException;

/**
 * Authorization exception
 * Basic exceptions, exceptions related to custom authorization need to inherit
 * <p>
 * 授权异常
 * 基础异常，自定义授权相关异常需要继承
 *
 * @author NorthLan
 * @date 2021/7/29
 * @url https://blog.noahlan.com
 */
public class AuthorizationException extends SecurityException {
    private static final long serialVersionUID = -839288489115123864L;

    public AuthorizationException(IEnum<String> spec) {
        super(spec);
    }

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String errorCode, String message) {
        super(errorCode, message);
    }
}
