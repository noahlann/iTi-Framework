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

package org.lan.iti.iha.security.clientdetails;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * A representation of a client registration with an OAuth 2.0 Authorization Server.
 *
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-2" target="_blank" >Section 2 Client Registration</a>
 */
@Data
@Accessors(chain = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ClientDetails implements Serializable {
    private static final long serialVersionUID = 3276335437589709597L;

    private String id;

    /**
     * client id, A unique random string
     */
    private String clientId;

    /**
     * client secret, A random string
     */
    private String clientSecret;

    /**
     * Client name
     */
    private String clientName;

    /**
     * Callback urls after successful login, separated by COMMA
     */
    private String redirectUris;

    /**
     * The scope of permissions granted to the application, separated by SPACE, for example: `openid email phone`
     */
    private String scopes;

    /**
     * Callback urls after successful logout, separated by COMMA
     */
    private String logoutRedirectUris;

    /* Token Settings */
    private Long accessTokenTimeToLive;
    private Long refreshTokenTimeToLive;
    private Long codeTimeToLive;
    private Long idTokenTimeToLive;
    private boolean reuseRefreshToken;

    /* Client Settings */
    private boolean requireProofKey;
    private boolean autoApprove;

    /**
     * Custom second-level domain name
     */
    private String siteDomain;

    /**
     * App logo url
     */
    private String logo;

    /**
     * The status of the application, when it is false, login is not allowed
     */
    private boolean available;

    /**
     * Application description
     */
    private String description;

    /**
     * The type of authorization granted to the application, separated by SPACE
     */
    private String grantTypes;

    /**
     * The response type granted by the application, separated by SPACE
     */
    private String responseTypes;

    /**
     * Encryption method of pkce challenge code
     */
    private String codeChallengeMethod;
}
