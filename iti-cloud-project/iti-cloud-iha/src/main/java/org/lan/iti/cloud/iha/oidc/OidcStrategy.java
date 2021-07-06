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

package org.lan.iti.cloud.iha.oidc;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import org.lan.iti.cloud.iha.core.cache.IhaCache;
import org.lan.iti.cloud.iha.core.config.AuthenticateConfig;
import org.lan.iti.cloud.iha.core.config.IhaConfig;
import org.lan.iti.cloud.iha.core.exception.IhaException;
import org.lan.iti.cloud.iha.core.exception.IhaOidcException;
import org.lan.iti.cloud.iha.core.repository.IhaUserRepository;
import org.lan.iti.cloud.iha.core.result.IhaErrorCode;
import org.lan.iti.cloud.iha.core.result.IhaResponse;
import org.lan.iti.cloud.iha.oauth2.OAuth2Strategy;
import org.lan.iti.cloud.iha.oauth2.OAuthConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * OpenID Connect 1.0 is a simple identity layer on top of the OAuth 2.0 protocol.
 * It enables Clients to verify the identity of the End-User based on the authentication performed by an Authorization Server,
 * as well as to obtain basic profile information about the End-User in an interoperable and REST-like manner.
 *
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html" target="_blank">OpenID Connect Core 1.0 incorporating errata set 1</a>
 */
public class OidcStrategy extends OAuth2Strategy {
    public OidcStrategy(IhaUserRepository userRepository, IhaConfig ihaConfig) {
        super(userRepository, ihaConfig);
    }

    public OidcStrategy(IhaUserRepository userRepository, IhaConfig ihaConfig, IhaCache ihaCache) {
        super(userRepository, ihaConfig, ihaCache);
    }

    @Override
    public IhaResponse authenticate(AuthenticateConfig config, HttpServletRequest request, HttpServletResponse response) {
        try {
            this.checkAuthenticateConfig(config, OidcConfig.class);
        } catch (IhaException e) {
            return IhaResponse.error(e.getCode(), e.getMessage());
        }
        OidcConfig oidcConfig = (OidcConfig) config;
        if (ObjectUtil.isNull(oidcConfig.getIssuer())) {
            return IhaResponse.error(IhaErrorCode.MISS_ISSUER);
        }

        String issuer = oidcConfig.getIssuer();

        OidcDiscovery discoveryDto;

        IhaCache japCache = this.context.getCache();

        String discoveryCacheKey = OidcConstants.DISCOVERY_CACHE_KEY.concat(issuer);
        if (japCache.containsKey(discoveryCacheKey)) {
            discoveryDto = (OidcDiscovery) japCache.get(discoveryCacheKey);
        } else {
            try {
                discoveryDto = OidcUtil.getOidcDiscovery(issuer);
                japCache.set(discoveryCacheKey, discoveryDto);
            } catch (IhaOidcException e) {
                return IhaResponse.error(e.getCode(), e.getMessage());
            }
        }

        oidcConfig.setAuthorizationUrl(discoveryDto.getAuthorizationEndpoint())
                .setTokenUrl(discoveryDto.getTokenEndpoint())
                .setUserinfoUrl(discoveryDto.getUserinfoEndpoint());

        OAuthConfig oAuthConfig = BeanUtil.copyProperties(oidcConfig, OAuthConfig.class);
        return super.authenticate(oAuthConfig, request, response);
    }
}
