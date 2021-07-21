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

package org.lan.iti.iha.server.model.enums;

/**
 * When accessing the api, the token usage mode supports the following four situations:
 * <p>
 * 1. Set {@code bearer access token} in the request header: {@link TokenAuthenticationMethod#TOKEN_HEADER}
 * <p>
 * 2. Set access token in cookie: {@link TokenAuthenticationMethod#TOKEN_COOKIE}
 * <p>
 * 3. url: {@link TokenAuthenticationMethod#TOKEN_URL}
 * <p>
 * 4. All of the above support:{@link TokenAuthenticationMethod#ALL}
 *
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
public enum TokenAuthenticationMethod {
    /**
     * request header
     */
    TOKEN_HEADER,
    /**
     * cookie
     */
    TOKEN_COOKIE,
    /**
     * url
     */
    TOKEN_URL,
    /**
     * 支持全部
     */
    ALL
}
