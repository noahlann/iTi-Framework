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

package org.lan.iti.cloud.iha.oauth2;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum GrantType {
    /**
     * Authorization Code Grant
     *
     * @see <a href="https://tools.ietf.org/html/rfc6749#section-1.3.1" target="_blank">RFC 6749 (OAuth 2.0), 1.3.1.  Authorization Code</a>
     * @see <a href="http://tools.ietf.org/html/rfc6749#section-4.1.3" target="_blank">RFC 6749 (OAuth 2.0), 4.1.3. Access Token Request</a>
     * @see <a href="http://openid.net/specs/openid-connect-core-1_0.html#TokenEndpoint" target="_blank">OpenID Connect Core 1.0, 3.1.3. Token Endpoint</a>
     */
    AUTHORIZATION_CODE("authorization_code"),

    /**
     * The implicit grant is a simplified authorization code flow optimized for clients implemented in a browser using a scripting language such as JavaScript.
     *
     * @see <a href="https://tools.ietf.org/html/rfc6749#section-1.3.2" target="_blank">RFC 6749 (OAuth 2.0), 1.3.2.  Implicit</a>
     */
    IMPLICIT("implicit"),

    /**
     * Resource Owner Password Credentials Grant
     *
     * @see <a href="https://tools.ietf.org/html/rfc6749#section-1.3.3" target="_blank">RFC 6749 (OAuth 2.0), 1.3.3.  Resource Owner Password Credentials</a>
     * @see <a href="http://tools.ietf.org/html/rfc6749#section-4.3.2" target="_blank">RFC 6749 (OAuth 2.0), 4.3.2. Access Token Request</a>
     */
    PASSWORD("password"),

    /**
     * Client Credentials Grant
     *
     * @see <a href="https://tools.ietf.org/html/rfc6749#section-1.3.4" target="_blank">RFC 6749 (OAuth 2.0), 1.3.4. Client Credentials</a>
     * @see <a href="http://tools.ietf.org/html/rfc6749#section-4.4.2" target="_blank">RFC 6749 (OAuth 2.0), 4.4.2. Access Token Request</a>
     */
    CLIENT_CREDENTIALS("client_credentials"),

    /**
     * The grant type used when refreshing the token
     *
     * @see <a href="http://tools.ietf.org/html/rfc6749#section-6" target="_blank">RFC 6749 (OAuth 2.0), 6. Refreshing an Access Token</a>
     * @see <a href="http://openid.net/specs/openid-connect-core-1_0.html#RefreshTokens" target="_blank">OpenID Connect Core 1.0, 12. Using Refresh Tokens</a>
     */
    REFRESH_TOKEN("refresh_token"),

    /**
     * The grant type used when obtaining the access token
     */
    TOKEN("token"),
    ;
    private final String type;

    public static List<String> grantTypes() {
        return Arrays.stream(GrantType.values())
                .map(GrantType::getType)
                .collect(Collectors.toList());
    }

}
