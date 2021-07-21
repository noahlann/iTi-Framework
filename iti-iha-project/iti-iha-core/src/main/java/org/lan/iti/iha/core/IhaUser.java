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

package org.lan.iti.iha.core;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 基本用户
 *
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@Data
@Accessors(chain = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class IhaUser implements Serializable {
    private static final long serialVersionUID = -8066382503410442934L;

    /**
     * The id of the user in the business system
     */
    private String id;

    /**
     * zoneId
     */
    private String zoneId;

    /**
     * User name in the business system
     */
    private String username;

    /**
     * User nickname in the business system
     */
    private String nickname;

    /**
     * User mobile in business system
     */
    private String mobile;

    /**
     * User email in business system
     */
    private String email;

    /**
     * User avatar in business system
     */
    private String avatar;

    /**
     * Description in business system
     */
    private String description;

    /**
     * User password in business system
     */
    private String password;

    /**
     * Profile url in business system
     */
    private String profile;

    /**
     * Additional information about users in the developer's business system returned when obtaining user data.
     * Please do not save private data, such as secret keys, token.
     */
    private Map<String, Object> additionalInformation;

    /**
     * Roles in business system
     */
    private List<Map<String, String>> roles;

    /**
     * token
     */
    private String token;

    /**
     * remember me
     */
    private boolean rememberMe;
}
