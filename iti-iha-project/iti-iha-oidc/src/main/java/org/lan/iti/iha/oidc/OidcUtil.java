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

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.xkcoding.http.HttpUtil;
import com.xkcoding.json.JsonUtil;
import com.xkcoding.json.util.Kv;
import lombok.experimental.UtilityClass;
import org.lan.iti.iha.core.exception.IhaOidcException;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@UtilityClass
public class OidcUtil {

    /**
     * Get the IDP service configuration
     *
     * @param issuer IDP identity providers
     * @return OidcDiscoveryDto
     */
    public static OidcDiscovery getOidcDiscovery(String issuer) {
        if (StrUtil.isBlank(issuer)) {
            throw new IhaOidcException("Missing IDP Discovery Url.");
        }
        String discoveryUrl = issuer.concat(OidcConstants.DISCOVERY_URL);

        String response;
        try {
            response = HttpUtil.get(discoveryUrl);
        } catch (Exception e) {
            throw new IhaOidcException("Cannot access discovery url: " + discoveryUrl);
        }
        Kv oidcDiscoveryInfo = JsonUtil.parseKv(response);
        if (MapUtil.isEmpty(oidcDiscoveryInfo)) {
            throw new IhaOidcException("Unable to parse IDP service discovery configuration information.");
        }

        return OidcDiscovery.builder()
                .issuer(oidcDiscoveryInfo.getString(OidcDiscoveryParams.ISSUER))
                .authorizationEndpoint(oidcDiscoveryInfo.getString(OidcDiscoveryParams.AUTHORIZATION_ENDPOINT))
                .tokenEndpoint(oidcDiscoveryInfo.getString(OidcDiscoveryParams.TOKEN_ENDPOINT))
                .userinfoEndpoint(oidcDiscoveryInfo.getString(OidcDiscoveryParams.USERINFO_ENDPOINT))
                .endSessionEndpoint(oidcDiscoveryInfo.getString(OidcDiscoveryParams.END_SESSION_ENDPOINT))
                .jwksUri(oidcDiscoveryInfo.getString(OidcDiscoveryParams.JWKS_URI))
                .build();
    }
}
