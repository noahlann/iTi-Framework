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

package org.lan.iti.cloud.iha.server.endpoint;

import org.lan.iti.cloud.iha.server.model.OidcDiscovery;
import org.lan.iti.cloud.iha.server.oidc.OidcUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
public class DiscoveryEndpoint extends AbstractEndpoint {

    /**
     * OpenID Provider Configuration Response.
     * <p>
     * For multiple users (can be users, organizations, enterprises, etc.), different configurations can be generated through {@code identity}.
     * <p>
     * Such as the following scenario:
     * <p>
     * The issuer of idp is `http://localhost`, and the api in the idp is distinguished according to the user ID.
     * <p>
     *
     * @param request current HTTP request
     * @return OpenID Provider Configuration
     * @see <a href="https://tools.ietf.org/html/draft-ietf-oauth-discovery-06">https://tools.ietf.org/html/draft-ietf-oauth-discovery-06</a>
     * @see <a href="https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderMetadata" target="_blank">OpenID Provider Metadata</a>
     * @see <a href="https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderConfigurationRequest">OpenID Provider Configuration Response</a>
     */
    public OidcDiscovery getOidcDiscovery(HttpServletRequest request) {
        return OidcUtil.getOidcDiscovery(request);
    }

    /**
     * Get the public key of the encrypted token (can be used to decrypt the token)
     *
     * <p>
     * For multiple users (can be users, organizations, enterprises, etc.), The public key information of different users can be obtained through {@code identity}.
     * <p>
     * Such as the following scenario:
     * <p>
     * Different users are assigned different keys in the idp system. When the {@code identity} is not empty,
     * <p>
     * the method will first obtain the user's certificate through the {@code identity}, and then generate the public key through the certificate
     *
     * @param identity identity
     * @return public key
     */
    public String getJwksPublicKey(String identity) {
        return OidcUtil.getJwksPublicKey(identity);
    }
}
