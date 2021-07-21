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

package org.lan.iti.iha.oidc;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * OpenID Provider Issuer discovery is the process of determining the location of the OpenID Provider.
 * <p>
 * For the properties defined by this class, please refer to [3.  OpenID Provider Metadata]
 *
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 * @see <a href="https://openid.net/specs/openid-connect-discovery-1_0.html" target="_blank">OpenID Connect Discovery 1.0 incorporating errata set 1</a>
 * @see <a href="https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderMetadata" target="_blank">3.  OpenID Provider Metadata</a>
 */
@Data
@Accessors(chain = true)
@SuperBuilder(toBuilder = true)
public class OidcDiscovery implements Serializable {
    private static final long serialVersionUID = -1161710064116885246L;

    /**
     * Identity provider URL
     */
    private String issuer;

    /**
     * URL of the OP's OAuth 2.0 Authorization Endpoint
     */
    private String authorizationEndpoint;

    /**
     * URL of the OP's OAuth 2.0 Token Endpoint
     */
    private String tokenEndpoint;

    /**
     * URL of the OP's UserInfo Endpoint
     */
    private String userinfoEndpoint;

    /**
     * URL of the OP's Logout Endpoint
     */
    private String endSessionEndpoint;

    /**
     * URL of the OP's JSON Web Key Set [JWK] document
     *
     * @see <a href="https://openid.net/specs/openid-connect-discovery-1_0.html#JWK" target="_blank">JWK</a>
     */
    private String jwksUri;
}
