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

package org.lan.iti.iha.core.result;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 错误代码
 *
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum IhaResponseCode {
    SUCCESS(200, "success."),
    NOT_LOGGED_IN(401, "Not logged in."),
    ERROR(500, "An error occurred in the system, please refer to the error message."),
    NOT_EXIST_USER(1000, "The user does not exist."),
    INVALID_PASSWORD(1001, "Passwords don't match."),
    INVALID_REMEMBER_ME_COOKIE(1002, "Illegal remember me cookie."),
    UNABLE_SAVE_USERINFO(1003, "Unable to save user information."),
    MISS_AUTH_CONFIG(1004, "AuthConfig in SocialStrategy is required."),
    MISS_AUTHENTICATE_CONFIG(1005, "AuthenticateConfig is required."),
    MISS_ISSUER(1006, "OidcStrategy requires a issuer option."),
    MISS_CREDENTIALS(1007, "Missing credentials"),
    ;
    private final int code;
    private final String message;
}
