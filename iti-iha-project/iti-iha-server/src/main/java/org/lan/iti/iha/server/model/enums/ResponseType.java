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
public enum ResponseType {
    /**
     * https://tools.ietf.org/html/rfc6749#section-3.1.1
     * https://tools.ietf.org/html/rfc6749#section-4.1.1
     */
    CODE("code"),
    /**
     * "token" for requesting an access token (implicit grant) as described
     * https://tools.ietf.org/html/rfc6749#section-4.2.1
     */
    TOKEN("token"),
    /**
     * a registered extension value as described by Section 8.4.
     * https://tools.ietf.org/html/rfc6749#section-8.4
     */
    ID_TOKEN("id_token"),
    ID_TOKEN_TOKEN("id_token token"),
    CODE_ID_TOKEN("code id_token"),
    CODE_TOKEN("code token"),
    CODE_ID_TOKEN_TOKEN("code id_token token"),

    /**
     * https://openid.net/specs/oauth-v2-multiple-response-types-1_0.html#none
     */
    NONE("none"),
    ;
    private final String type;

    public static List<String> responseTypes() {
        return Arrays.stream(ResponseType.values())
                .map(ResponseType::getType)
                .collect(Collectors.toList());
    }
}
