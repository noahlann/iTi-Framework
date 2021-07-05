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

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
public interface IhaServerConstants {
    String SLASH = "/";

    /**
     * The default validity period of the authorization code is 10 minutes (600 seconds)
     */
    long AUTHORIZATION_CODE_ACTIVITY_TIME = 600;
    /**
     * The default validity period of access token is 30 days (2592000 seconds)
     */
    long ACCESS_TOKEN_ACTIVITY_TIME = 2592000;
    /**
     * The default validity period of refresh token is 365 days (31536000 seconds)
     */
    long REFRESH_TOKEN_ACTIVITY_TIME = 31536000;
    /**
     * The default validity period of id token is 365 days (31536000 seconds)
     */
    long ID_TOKEN_ACTIVITY_TIME = 31536000;

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
    String OAUTH_USERINFO_CACHE_KEY = IHA_SERVER_OAUTH_CACHE_KEY + "USERINFO";

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
    String RESPONSE_MODE = "response_mode";
    String DISPLAY = "display";
    String PROMPT = "prompt";
    String MAX_AGE = "max_age";
    String ID_TOKEN_HINT = "id_token_hint";
    String AUTOAPPROVE = "autoapprove";

    /**
     * {@code auth_time} - the time when the End-User authentication occurred
     */
    String AUTH_TIME = "auth_time";

    /**
     * {@code acr} - the Authentication Context Class Reference
     */
    String ACR = "acr";
}
