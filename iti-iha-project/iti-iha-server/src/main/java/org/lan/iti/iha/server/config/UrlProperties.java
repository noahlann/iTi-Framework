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

/**
 * UrlProperties
 *
 * @author NorthLan
 * @date 2021/8/2
 * @url https://blog.noahlan.com
 */
@Data
@Accessors(chain = true)
public class UrlProperties {
    public static final String DEFAULT_TOKEN_URL = "/oauth2/token";
    public static final String DEFAULT_ERROR_URL = "/oauth2/error";
    public static final String DEFAULT_AUTHORIZE_URL = "/oauth2/authorize";
    public static final String DEFAULT_AUTHORIZE_AUTO_APPROVE_URL = "/oauth2/authorize/auto";
    public static final String DEFAULT_LOGIN_URL = "/oauth2/login";
    public static final String DEFAULT_USERINFO_URL = "/oauth2/userinfo";
    public static final String DEFAULT_REGISTRATION_URL = "/oauth2/registration";
    public static final String DEFAULT_END_SESSION_URL = "/oauth2/logout";
    public static final String DEFAULT_CHECK_SESSION_URL = "/oauth2/check_session";
    public static final String DEFAULT_LOGOUT_REDIRECT_URL = "/";
    public static final String DEFAULT_JWKS_URL = "/.well-known/jwks.json";
    public static final String DEFAULT_DISCOVERY_URL = "/.well-known/openid-configuration";
    public static final String DEFAULT_CONFIRM_URL = "/oauth2/confirm";

    private String tokenUrl = DEFAULT_TOKEN_URL;
    private String errorUrl = DEFAULT_ERROR_URL;
    private String authorizeUrl = DEFAULT_AUTHORIZE_URL;
    private String authorizeAutoApproveUrl = DEFAULT_AUTHORIZE_AUTO_APPROVE_URL;
    private String loginUrl = DEFAULT_LOGIN_URL;
    private String userinfoUrl = DEFAULT_USERINFO_URL;
    private String registrationUrl = DEFAULT_REGISTRATION_URL;
    private String endSessionUrl = DEFAULT_END_SESSION_URL;
    private String checkSessionUrl = DEFAULT_CHECK_SESSION_URL;
    private String logoutRedirectUrl = DEFAULT_LOGOUT_REDIRECT_URL;
    private String jwksUrl = DEFAULT_JWKS_URL;
    private String discoveryUrl = DEFAULT_DISCOVERY_URL;
    private String confirmUrl = DEFAULT_CONFIRM_URL;

    /**
     * Login page url
     */
    private String loginPageUrl = loginUrl;

    /**
     * When the login page is not provided by an authorized service (the login page is hosted by other services), this configuration needs to be turned on
     */
    private boolean externalLoginPageUrl = false;

    /**
     * The user confirms the authorized url, the default is {@code issuer + /oauth/confirm}
     */
    private String confirmPageUrl = confirmUrl;

    /**
     * When the authorization confirmation page is not provided by an authorized service (the authorization confirmation page is hosted by other services),
     * this configuration needs to be turned on
     */
    private boolean externalConfirmPageUrl = false;
}
