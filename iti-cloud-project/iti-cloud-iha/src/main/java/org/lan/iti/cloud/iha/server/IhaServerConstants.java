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

package org.lan.iti.cloud.iha.server;

import java.time.Duration;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
public interface IhaServerConstants {
    String SLASH = "/";
    String SEPARATOR_COMMA = ",";

    String TOKEN_URL = "/oauth2/token";
    String ERROR_URL = "/oauth2/error";
    String AUTHORIZE_URL = "/oauth2/authorize";
    String AUTHORIZE_AUTO_APPROVE_URL = "/oauth2/authorize/auto";
    String LOGIN_URL = "/oauth2/login";
    String USERINFO_URL = "/oauth2/userinfo";
    String REGISTRATION_URL = "/oauth2/registration";
    String END_SESSION_URL = "/oauth2/logout";
    String CHECK_SESSION_URL = "/oauth2/check_session";
    String LOGOUT_REDIRECT_URL = "/";
    String JWKS_URL = "/.well-known/jwks.json";
    String DISCOVERY_URL = "/.well-known/openid-configuration";
    String CONFIRM_URL = "/oauth2/confirm";

    /**
     * The default validity period of the authorization code is 10 minutes (600 seconds)
     */
    Duration AUTHORIZATION_CODE_ACTIVITY_TIME = Duration.ofMinutes(10L);
    /**
     * The default validity period of access token is 7 days
     */
    Duration ACCESS_TOKEN_ACTIVITY_TIME = Duration.ofDays(7);
    /**
     * The default validity period of refresh token is 30 days
     */
    Duration REFRESH_TOKEN_ACTIVITY_TIME = Duration.ofDays(30);
    /**
     * The default validity period of id token is 365 days
     */
    Duration ID_TOKEN_ACTIVITY_TIME = Duration.ofDays(365);

    /**
     * token header name
     */
    String AUTHORIZATION_HEADER_NAME = "Authorization";

    /**
     * Token Type
     */
    String TOKEN_TYPE_BEARER = "Bearer";

    /**
     * Cache key of oauth authorized user
     */
    String IHA_SERVER_OAUTH_CACHE_KEY = "IHA_SERVER:OAUTH2:";

    /**
     * Cache key of oauth authorized user
     */
    String OAUTH_USER_CACHE_KEY = IHA_SERVER_OAUTH_CACHE_KEY + "USERINFO";

    /**
     * Cache the key of access token
     */
    String OAUTH_ACCESS_TOKEN_CACHE_KEY = IHA_SERVER_OAUTH_CACHE_KEY + "ACCESS_TOKEN:";

    /**
     * Cache the key of refresh token
     */
    String OAUTH_REFRESH_TOKEN_CACHE_KEY = IHA_SERVER_OAUTH_CACHE_KEY + "REFRESH_TOKEN:";

    /**
     * Cache the key of the oauth code
     */
    String OAUTH_CODE_CACHE_KEY = IHA_SERVER_OAUTH_CACHE_KEY + "CODE:";

    String UID = "uid";
    String AUTOAPPROVE = "autoapprove";
}
