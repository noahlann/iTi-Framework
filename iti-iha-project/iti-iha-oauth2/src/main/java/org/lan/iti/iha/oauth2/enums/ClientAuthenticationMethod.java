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

package org.lan.iti.iha.oauth2.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The client secret authentication method supports the following four situations
 * <p>
 * 1. Post parameter: {@link ClientAuthenticationMethod#CLIENT_SECRET_POST}
 * <p>
 * 2. The basic format string in the request header:{@link ClientAuthenticationMethod#CLIENT_SECRET_BASIC}
 * <p>
 * 3. url: {@link ClientAuthenticationMethod#NONE}
 * <p>
 * 4. All of the above support: {@link ClientAuthenticationMethod#ALL}
 *
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
public enum ClientAuthenticationMethod {
    /**
     * Post parameter
     */
    CLIENT_SECRET_POST,
    /**
     * The basic format string in the request header
     */
    CLIENT_SECRET_BASIC,
    /**
     * url
     */
    NONE,
    /**
     * All of the above support
     */
    ALL;

    public static List<String> getAllMethods() {
        return Arrays.stream(ClientAuthenticationMethod.values())
                .filter((method) -> method != ALL)
                .map((method) -> method.name().toLowerCase())
                .collect(Collectors.toList());
    }

    public String getMethod() {
        return this.name().toLowerCase();
    }
}
