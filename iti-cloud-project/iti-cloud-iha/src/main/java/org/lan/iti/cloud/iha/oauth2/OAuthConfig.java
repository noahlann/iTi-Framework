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

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.lan.iti.cloud.iha.core.config.AuthenticateConfig;
import org.lan.iti.cloud.iha.oauth2.pkce.PkceCodeChallengeMethod;

/**
 * Configuration file of oauth2 module
 *
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class OAuthConfig extends AuthenticateConfig {
    /**
     * Name of OAuth platform
     */
    private String platform;

    /**
     * identifies client to service provider
     */
    private String clientId;

    /**
     * secret used to establish ownership of the client identifer
     */
    private String clientSecret;

    /**
     * URL to which the service provider will redirect the user after obtaining authorization
     */
    private String callbackUrl;

    /**
     * URL used to obtain an authorization grant
     */
    private String authorizationUrl;

    /**
     * URL used to obtain an access token
     */
    private String tokenUrl;

    /**
     * URL used to obtain an userinfo
     */
    private String userinfoUrl;

    /**
     * The value MUST be one of "code" for requesting an
     * authorization code as described by Section 4.1.1 (<a href="https://tools.ietf.org/html/rfc6749#section-4.1.1" target="_blank">https://tools.ietf.org/html/rfc6749#section-4.1.1</a>),
     * "token" for requesting an access token (implicit grant) as described by Section 4.2.1 (<a href="https://tools.ietf.org/html/rfc6749#section-4.2.1" target="_blank">https://tools.ietf.org/html/rfc6749#section-4.2.1</a>),
     * or a registered extension value as described by Section 8.4 (<a href="https://tools.ietf.org/html/rfc6749#section-8.4" target="_blank">https://tools.ietf.org/html/rfc6749#section-8.4</a>).
     */
    private String responseType = OAuth2ResponseType.NONE;

    /**
     * The optional value is: {@code authorization_code}, {@code password}, {@code client_credentials}
     */
    private GrantType grantType = GrantType.AUTHORIZATION_CODE;

    /**
     * The scope supported by the OAuth platform
     */
    private String[] scopes;

    /**
     * An opaque value used by the client to maintain
     * state between the request and callback.  The authorization
     * server includes this value when redirecting the user-agent back
     * to the client.
     */
    private String state;

    /**
     * The scope supported by the OAuth platform
     */
    private boolean enablePkce;

    /**
     * After the pkce enhancement protocol is enabled, the generation method of challenge code derived from
     * the code verifier sent in the authorization request is `s256` by default
     *
     * @see <a href="https://tools.ietf.org/html/rfc7636#section-4.3" target="_blank"> Client Sends the Code Challenge with the Authorization Request</a>
     */
    private PkceCodeChallengeMethod codeChallengeMethod = PkceCodeChallengeMethod.S256;

    /**
     * The username in `Resource Owner Password Credentials Grant`
     *
     * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.3" target="_blank">https://tools.ietf.org/html/rfc6749#section-4.3</a>
     */
    private String username;

    /**
     * The password in `Resource Owner Password Credentials Grant`
     *
     * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.3" target="_blank">https://tools.ietf.org/html/rfc6749#section-4.3</a>
     */
    private String password;

    /**
     * In pkce mode, the expiration time of codeverifier, in milliseconds, default is 3 minutes
     */
    private long codeVerifierTimeout = 180000;

    /**
     * When {@code verifyState} is true, it will check whether the state in authorization request is consistent with that in callback request
     */
    private boolean verifyState = true;

    /**
     * method of userInfo endpoint, default is POST
     * <p>
     * Different third-party platforms may use different request methods,
     * and some third-party platforms have limited request methods, such as post and get.
     */
    private OAuth2EndpointMethodType userInfoEndpointMethodType = OAuth2EndpointMethodType.POST;

    /**
     * method of accessToken endpoint, default is POST
     * <p>
     * Different third-party platforms may use different request methods,
     * and some third-party platforms have limited request methods, such as post and get.
     */
    private OAuth2EndpointMethodType accessTokenEndpointMethodType = OAuth2EndpointMethodType.POST;
}
