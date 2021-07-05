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

package org.lan.iti.cloud.iha.server.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.lan.iti.cloud.iha.server.model.enums.ClientAuthenticationMethod;
import org.lan.iti.cloud.iha.server.model.enums.TokenAuthenticationMethod;

import java.util.Collections;
import java.util.List;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@Data
@Accessors(chain = true)
public class IhaServerConfig {

    /**
     * Get the user name from request through {@code request.getParameter(`usernameField`)}, which defaults to "username"
     */
    private String usernameField = "username";
    /**
     * Get the password from request through {@code request.getParameter(`passwordField`)}, which defaults to "password"
     */
    private String passwordField = "password";

    private boolean enableDynamicIssuer;
    /**
     * Identity provider
     */
    private String issuer;
    /**
     * Login url, the default is {@code /oauth/login}
     */
    private String loginUrl;
    /**
     * error url, the default is {@code /oauth/error}
     */
    private String errorUrl;
    /**
     * Authorized url, the default is {@code /oauth/authorize}
     */
    private String authorizeUrl;
    /**
     * Automatically authorized url (do not display the authorization page), Must support get request method,
     * the default is {@code /oauth/authorize/auto}
     */
    private String authorizeAutoApproveUrl;
    /**
     * token url, the default is {@code /oauth/token}
     */
    private String tokenUrl;
    /**
     * userinfo url, the default is {@code /oauth/userinfo}
     */
    private String userinfoUrl;
    /**
     * Register the the client detail, the default is {@code /oauth/registration}
     */
    private String registrationUrl;
    /**
     * logout url, the default is {@code /oauth/logout}
     */
    private String endSessionUrl;
    /**
     * check session url, the default is {@code /oauth/check_session}
     */
    private String checkSessionUrl;
    /**
     * After logout, redirect to {@code logoutRedirectUrl}. Default is `/`
     */
    private String logoutRedirectUrl;
    /**
     * public key url, the default is {@code /.well-known/jwks.json}
     */
    private String jwksUrl;
    /**
     * Get open id provider metadata, the default is {@code /.well-known/openid-configuration}
     */
    private String discoveryUrl;
    /**
     * Login page url, the default is {@link org.lan.iti.cloud.iha.server.config.IhaServerConfig#loginUrl}
     */
    private String loginPageUrl;

    /**
     * When the login page is not provided by an authorized service (the login page is hosted by other services), this configuration needs to be turned on
     */
    private boolean externalLoginPageUrl;

    /**
     * The user confirms the authorized url, the default is {@code issuer + /oauth/confirm}
     */
    private String confirmPageUrl;

    /**
     * When the authorization confirmation page is not provided by an authorized service (the authorization confirmation page is hosted by other services),
     * this configuration needs to be turned on
     */
    private boolean externalConfirmPageUrl;

    /**
     * When requesting api, the way to pass token
     */
    private List<TokenAuthenticationMethod> tokenAuthMethods = Collections.singletonList(TokenAuthenticationMethod.ALL);

    /**
     * When requesting the token endpoint, the way to pass the client secret
     */
    private List<ClientAuthenticationMethod> clientSecretAuthMethods = Collections.singletonList(ClientAuthenticationMethod.ALL);

    /**
     * Generate/verify the global configuration of jwt token.
     * If the caller needs to configure a set of jwt config for each client,
     * you can specify jwt config when obtaining the token.
     */
    private JwtConfig jwtConfig = new JwtConfig();
}
