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

package org.lan.iti.iha.social.exception;

import org.lan.iti.common.core.support.IEnum;
import org.lan.iti.iha.security.exception.authentication.AuthenticationException;

/**
 * 身份认证：社交登录相关异常
 *
 * @author NorthLan
 * @date 2021/8/16
 * @url https://blog.noahlan.com
 */
public class SocialAuthenticationException extends AuthenticationException {
    private static final long serialVersionUID = -6570723460616911437L;

    public SocialAuthenticationException(IEnum<String> spec) {
        super(spec);
    }

    public SocialAuthenticationException(String message) {
        super(message);
    }

    public SocialAuthenticationException(String errorCode, String message) {
        super(errorCode, message);
    }
}
