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

package org.lan.iti.cloud.iha.simple;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.lan.iti.cloud.iha.core.config.AuthenticateConfig;

import java.time.Duration;

/**
 * @author NorthLan
 * @date 2021-07-07
 * @url https://noahlan.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class SimpleConfig extends AuthenticateConfig {
    /**
     * Get the user principal(username/mobile/email) from request through {@code request.getParameter(`usernameField`)}, which defaults to "username"
     */
    private String principalField = "username";

    /**
     * Get the password from request through {@code request.getParameter(`passwordField`)}, which defaults to "password"
     */
    private String passwordField = "password";

    /**
     * Get the validation code from request through {@code request.getParameter(`codeField`)}, which defaults to "code"
     */
    private String codeField = "code";

    /**
     * Get the login type from request through {@code request.getParameter(`typeField`)}, which defaults to "type"
     */
    private String typeField = "type";

    /**
     * Get the extra data from request through {@code request.getParameter(`extra`)}, which defaults to "extra"
     */
    private String extraField = "extra";

    /**
     * Get the remember-me from request through {@code request.getParameter(`rememberMeField`)}, which defaults to "rememberMe"
     */
    private String rememberMeField = "rememberMe";

    /**
     * Default remember me cookie key
     */
    private String rememberMeCookieKey = "_iha_remember_me";

    /**
     * Remember me cookie expire, unit: second, default 60*60*24[24 hours]
     */
    private long rememberMeCookieMaxAge = Duration.ofHours(24).toMillis();

    /**
     * Remember me cookie domain
     */
    private String rememberMeCookieDomain;

    /**
     * Credential Encrypt Salt
     */
    private String credentialEncryptSalt = "_iha:rememberMe";
}
