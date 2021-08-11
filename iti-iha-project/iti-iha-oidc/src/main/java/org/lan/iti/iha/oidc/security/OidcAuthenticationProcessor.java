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
import org.lan.iti.iha.oauth2.OAuth2Config;
import org.lan.iti.iha.oauth2.security.OAuth2AuthenticationProcessor;
import org.lan.iti.iha.oauth2.security.OAuth2RequestParameter;
import org.lan.iti.iha.oidc.OidcConfig;
import org.lan.iti.iha.security.authentication.Authentication;
import org.lan.iti.iha.security.exception.AuthenticationException;
import org.lan.iti.iha.security.mgt.RequestParameter;
import org.lan.iti.iha.security.processor.ProcessChain;
import org.lan.iti.iha.security.processor.ProcessorType;

/**
 * @author NorthLan
 * @date 2021/8/4
 * @url https://blog.noahlan.com
 */
public class OidcAuthenticationProcessor extends OAuth2AuthenticationProcessor {

    @Override
    public boolean matches(String params) {
        return ProcessorType.OIDC.equals(params);
    }

    @Override
    public int getOrder() {
        return super.getOrder();
    }

    @Override
    public boolean support(RequestParameter parameter, Authentication authentication) {
        return authentication instanceof OidcAuthenticationToken &&
                parameter instanceof OidcRequestParameter;
    }

    @Override
    public Authentication process(RequestParameter parameter, Authentication authentication, ProcessChain chain) throws AuthenticationException {
        OidcRequestParameter oidcRequestParameter = (OidcRequestParameter) parameter;
        OidcConfig config = oidcRequestParameter.getConfig();

        OAuth2Config oAuth2Config = BeanUtil.copyProperties(config, OAuth2Config.class);
        OAuth2RequestParameter oAuth2RequestParameter = new OAuth2RequestParameter(parameter);
        oAuth2RequestParameter.setConfig(oAuth2Config);

        return super.process(oAuth2RequestParameter, authentication, chain);
    }
}
