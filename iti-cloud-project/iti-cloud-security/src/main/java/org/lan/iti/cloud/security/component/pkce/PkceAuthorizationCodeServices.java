/*
 *
 *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.cloud.security.component.pkce;

import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;

/**
 * Services for issuing and storing authorization codes. (PKCE)
 *
 * @author NorthLan
 * @date 2020-08-05
 * @url https://noahlan.com
 */
public interface PkceAuthorizationCodeServices extends AuthorizationCodeServices {
    // TODO 配置文件
    String KEY_CODE_CHALLENGE = "code_challenge";
    String KEY_CODE_CHALLENGE_METHOD = "code_challenge_method";
    String KEY_ENABLE_PKCE = "enablePkce";

    /**
     * Consume a authorization code and codeVerifier (PKCE)
     *
     * @param code         The authorization code to consume.
     * @param codeVerifier The codeVerifier to validate.
     * @return The authentications associated with the code.
     * @throws InvalidGrantException If the authorization code is invalid or expired.
     */
    OAuth2Authentication consumeAuthorizationCodeAndCodeVerifier(String code, String codeVerifier) throws InvalidGrantException;
}
