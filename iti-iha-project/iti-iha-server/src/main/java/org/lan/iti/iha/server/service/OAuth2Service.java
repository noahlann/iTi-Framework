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

package org.lan.iti.iha.server.service;

import org.lan.iti.iha.server.model.AuthorizationCode;
import org.lan.iti.iha.server.model.IhaServerRequestParam;
import org.lan.iti.iha.server.model.UserDetails;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
public interface OAuth2Service {
    /**
     * Generate authorization code
     *
     * @param param         Parameters requested by the client
     * @param userDetails          User Info
     * @param codeExpiresIn code expiration time
     * @return String
     */
    String createAuthorizationCode(IhaServerRequestParam param, UserDetails userDetails, Long codeExpiresIn);

    /**
     * Verification authorization code
     *
     * @param grantType grant Type
     * @param code      authorization code
     * @return AuthCode
     */
    AuthorizationCode validateAndGetAuthorizationCode(String grantType, String code);

    /**
     * When the pkce protocol is enabled, the code challenge needs to be verified
     *
     * @param codeVerifier code verifier
     * @param code         authorization code
     * @see <a href="https://tools.ietf.org/html/rfc7636">https://tools.ietf.org/html/rfc7636</a>
     */
    void validateAuthorizationCodeChallenge(String codeVerifier, String code);

    /**
     * Obtain auth code info by authorization code
     *
     * @param code authorization code
     * @return string
     */
    AuthorizationCode getCodeInfo(String code);

    /**
     * Delete authorization code
     *
     * @param code authorization code
     */
    void invalidateCode(String code);
}
