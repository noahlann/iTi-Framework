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

package org.lan.iti.iha.oidc.security;

import cn.hutool.core.bean.BeanUtil;
import org.lan.iti.common.core.util.StringUtil;
import org.lan.iti.iha.oauth2.OAuth2Config;
import org.lan.iti.iha.oauth2.security.OAuth2RequestParameter;
import org.lan.iti.iha.oauth2.util.OAuth2Util;
import org.lan.iti.iha.oidc.OidcConfig;
import org.lan.iti.iha.oidc.OidcDiscovery;
import org.lan.iti.iha.oidc.OidcUtil;
import org.lan.iti.iha.security.IhaSecurity;
import org.lan.iti.iha.security.authentication.Authentication;
import org.lan.iti.iha.security.exception.authentication.AuthenticationException;
import org.lan.iti.iha.security.mgt.RequestParameter;
import org.lan.iti.iha.security.processor.ProcessChain;

/**
 * @author NorthLan
 * @date 2021/8/4
 * @url https://blog.noahlan.com
 */
public class PreOidcAuthenticationProcessor extends AbstractOidcAuthenticationProcessor {
    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 1000;
    }

    @Override
    public boolean support(RequestParameter parameter, Authentication authentication) throws AuthenticationException {
        return authentication == null;
    }

    @Override
    public Authentication process(RequestParameter parameter, Authentication authentication, ProcessChain chain) throws AuthenticationException {
        OidcRequestParameter oidcRequestParameter = new OidcRequestParameter(parameter);
        OidcConfig oidcConfig = oidcRequestParameter.getConfig();
        if (oidcConfig == null) {
            throw new AuthenticationException("config must not be null");
        }
        // TODO check session
        OAuth2Util.checkOAuthConfig(oidcConfig);
        String issuer = oidcConfig.getIssuer();

        OidcDiscovery discovery = OidcUtil.getOidcDiscovery(issuer);
        // TODO cache oidc discovery

        if (StringUtil.hasEmpty(
                discovery.getTokenEndpoint(),
                discovery.getAuthorizationEndpoint(),
                discovery.getUserinfoEndpoint())) {
            throw new AuthenticationException("Unable to parse IAM service discovery configuration information.");
        }
        oidcConfig.setAuthorizationUrl(discovery.getAuthorizationEndpoint())
                .setTokenUrl(discovery.getTokenEndpoint())
                .setUserInfoUrl(discovery.getUserinfoEndpoint());

        OAuth2Config oAuth2Config = BeanUtil.copyProperties(oidcConfig, OAuth2Config.class);
        OAuth2RequestParameter oAuth2RequestParameter = new OAuth2RequestParameter(parameter);
        oAuth2RequestParameter.setConfig(oAuth2Config);

        // 执行OAuth2流程
        return chain.process(oAuth2RequestParameter, IhaSecurity.getSecurityManager().authenticate(oAuth2RequestParameter));
    }
}
