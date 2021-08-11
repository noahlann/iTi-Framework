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

package org.lan.iti.iha.server.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.lan.iti.iha.server.IhaServerConstants;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@Data
@Accessors(chain = true)
public class IhaServerConfig {

    /**
     * 开启动态Issuer
     */
    private boolean enableDynamicIssuer;

    /**
     * Identity provider
     */
    private String issuer;

    /**
     * Login url, the default is {@code /oauth/login}
     */
    private String loginUrl = IhaServerConstants.DEFAULT_LOGIN_URL;

    /**
     * error url, the default is {@code /oauth/error}
     */
    private String errorUrl = IhaServerConstants.DEFAULT_ERROR_URL;

    /**
     * Authorized url, the default is {@code /oauth/authorize}
     */
    private String authorizeUrl = IhaServerConstants.DEFAULT_AUTHORIZE_URL;

    /**
     * Automatically authorized url (do not display the authorization page), Must support get request method,
     * the default is {@code /oauth/authorize/auto}
     */
    private String authorizeAutoApproveUrl = IhaServerConstants.DEFAULT_AUTHORIZE_AUTO_APPROVE_URL;

    /**
     * token url, the default is {@code /oauth/token}
     */
    private String tokenUrl = IhaServerConstants.DEFAULT_TOKEN_URL;

    /**
     * userinfo url, the default is {@code /oauth/userinfo}
     */
    private String userinfoUrl = IhaServerConstants.DEFAULT_USERINFO_URL;

    /**
     * Register the the client detail, the default is {@code /oauth/registration}
     */
    private String registrationUrl = IhaServerConstants.DEFAULT_REGISTRATION_URL;

    /**
     * logout url, the default is {@code /oauth/logout}
     */
    private String endSessionUrl = IhaServerConstants.DEFAULT_END_SESSION_URL;

    /**
     * check session url, the default is {@code /oauth/check_session}
     */
    private String checkSessionUrl = IhaServerConstants.DEFAULT_CHECK_SESSION_URL;

    /**
     * After logout, redirect to {@code logoutRedirectUrl}. Default is `/logout`
     */
    private String logoutRedirectUrl = IhaServerConstants.DEFAULT_LOGOUT_REDIRECT_URL;

    /**
     * public key url, the default is {@code /.well-known/jwks.json}
     */
    private String jwksUrl = IhaServerConstants.DEFAULT_JWKS_URL;

    /**
     * Get open id provider metadata, the default is {@code /.well-known/openid-configuration}
     */
    private String discoveryUrl = IhaServerConstants.DEFAULT_DISCOVERY_URL;

    /**
     * Login page url, the default is {@link org.lan.iti.iha.server.config.IhaServerConfig#loginUrl}
     */
    private String loginPageUrl = loginUrl;

    /**
     * When the login page is not provided by an authorized service (the login page is hosted by other services), this configuration needs to be turned on
     */
    private boolean externalLoginPageUrl;

    /**
     * The user confirms the authorized url, the default is {@code issuer + /oauth/confirm}
     */
    private String confirmPageUrl = IhaServerConstants.DEFAULT_CONFIRM_URL;

    /**
     * When the authorization confirmation page is not provided by an authorized service (the authorization confirmation page is hosted by other services),
     * this configuration needs to be turned on
     */
    private boolean externalConfirmPageUrl;

    /**
     * Generate/verify the global configuration of jwt token.
     * If the caller needs to configure a set of jwt config for each client,
     * you can specify jwt config when obtaining the token.
     */
    private JwtConfig jwtConfig = new JwtConfig();
}
