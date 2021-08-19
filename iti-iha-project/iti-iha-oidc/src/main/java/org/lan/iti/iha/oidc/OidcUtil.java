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

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xkcoding.http.HttpUtil;
import lombok.experimental.UtilityClass;

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
            throw new SecurityException("Missing IDP Discovery Url.");
        }
        String discoveryUrl = issuer.concat(OidcConstants.DISCOVERY_URL);

        String response;
        try {
            response = HttpUtil.get(discoveryUrl);
        } catch (Exception e) {
            throw new SecurityException("Cannot access discovery url: " + discoveryUrl);
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            return objectMapper.readValue(response, OidcDiscovery.class);
        } catch (JsonProcessingException e) {
            throw new SecurityException("Unable to parse IDP service discovery configuration information.");
        }
    }
}
