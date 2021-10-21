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

package org.lan.iti.iha.simple;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.Duration;

/**
 * @author NorthLan
 * @date 2021-07-07
 * @url https://noahlan.com
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SimpleConfig {
    /**
     * Get the user principal(username/mobile/email) from request through {@code request.getParameter(`principalField`)}, which defaults to "username"
     */
    @Builder.Default
    private String principalField = "principal";

    /**
     * Get the password from request through {@code request.getParameter(`credentialsField`)}, which defaults to "password"
     */
    @Builder.Default
    private String credentialsField = "credentials";

    /**
     * Get the validation code from request through {@code request.getParameter(`codeField`)}, which defaults to "code"
     */
    @Builder.Default
    private String codeField = "code";

    /**
     * Get the login type from request through {@code request.getParameter(`typeField`)}, which defaults to "type"
     */
    @Builder.Default
    private String typeField = "type";

    /**
     * Get the extra data from request through {@code request.getParameter(`extra`)}, which defaults to "extra"
     */
    @Builder.Default
    private String extraField = "extra";

    /**
     * Get the remember-me from request through {@code request.getParameter(`rememberMeField`)}, which defaults to "rememberMe"
     */
    @Builder.Default
    private String rememberMeField = "rememberMe";

    /**
     * Default remember me cookie key
     */
    @Builder.Default
    private String rememberMeCookieKey = "_iha_remember_me";

    /**
     * Remember me cookie expire, unit: second, default 60*60*24[24 hours]
     */
    @Builder.Default
    private long rememberMeCookieMaxAge = Duration.ofHours(24).getSeconds();

    /**
     * Remember me cookie domain
     */
    private String rememberMeCookieDomain;

    /**
     * Credential Encrypt Salt
     */
    @Builder.Default
    private String credentialEncryptSalt = "_iha:rememberMe";
}
