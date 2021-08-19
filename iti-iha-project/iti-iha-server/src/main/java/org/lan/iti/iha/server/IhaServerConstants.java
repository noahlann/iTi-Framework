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

package org.lan.iti.iha.server;

import java.time.Duration;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
public interface IhaServerConstants {
    /**
     * The default validity period of the authorization code is 10 minutes (600 seconds)
     */
    long DEFAULT_AUTHORIZATION_CODE_TIME_TO_LIVE = Duration.ofMinutes(10L).getSeconds();
    /**
     * The default validity period of access token is 7 days
     */
    long DEFAULT_ACCESS_TOKEN_TIME_TO_LIVE = Duration.ofDays(7).getSeconds();
    /**
     * The default validity period of refresh token is 30 days
     */
    long DEFAULT_REFRESH_TOKEN_TIME_TO_LIVE = Duration.ofDays(30).getSeconds();
    /**
     * The default validity period of id token is 365 days
     */
    long DEFAULT_ID_TOKEN_TIME_TO_LIVE = Duration.ofDays(365).getSeconds();

    /**
     * Cache key of oauth authorized user
     */
    String IHA_SERVER_OAUTH_CACHE_KEY = "IHA_SERVER:OAUTH2:";

    /**
     * Cache the key of access token
     */
    String OAUTH_ACCESS_TOKEN_CACHE_KEY = IHA_SERVER_OAUTH_CACHE_KEY + "ACCESS_TOKEN:";

    /**
     * Cache the key of refresh token
     */
    String OAUTH_REFRESH_TOKEN_CACHE_KEY = IHA_SERVER_OAUTH_CACHE_KEY + "REFRESH_TOKEN:";

    /**
     * Cache the key of id token
     */
    String OAUTH_ID_TOKEN_CACHE_KEY = IHA_SERVER_OAUTH_CACHE_KEY + "ID_TOKEN:";

    /**
     * Cache the key of the oauth code
     */
    String OAUTH_CODE_CACHE_KEY = IHA_SERVER_OAUTH_CACHE_KEY + "CODE:";

    /**
     * uid
     */
    String UID = "uid";

    /**
     * appId
     */
    String APP_ID = "appId";

    /**
     * domain of app
     */
    String APP_DOMAIN = "domain";
}
