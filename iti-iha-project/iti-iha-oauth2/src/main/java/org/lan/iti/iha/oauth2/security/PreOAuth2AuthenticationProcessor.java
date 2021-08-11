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

package org.lan.iti.iha.oauth2.security;

import org.lan.iti.common.core.util.StringUtil;
import org.lan.iti.iha.oauth2.OAuth2Config;
import org.lan.iti.iha.oauth2.OAuth2ParameterNames;
import org.lan.iti.iha.oauth2.util.OAuth2Util;
import org.lan.iti.iha.security.authentication.Authentication;
import org.lan.iti.iha.security.exception.AuthenticationException;
import org.lan.iti.iha.security.exception.UnsupportedAuthenticationException;
import org.lan.iti.iha.security.mgt.RequestParameter;
import org.lan.iti.iha.security.processor.ProcessChain;

/**
 * AccessToken Grant - Pre process
 *
 * @author NorthLan
 * @date 2021/8/3
 * @url https://blog.noahlan.com
 */
public class PreOAuth2AuthenticationProcessor extends AbstractOAuth2AuthenticationProcessor {

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
        OAuth2RequestParameter oAuth2RequestParameter = new OAuth2RequestParameter(parameter);
        OAuth2Config oAuth2Config = oAuth2RequestParameter.getConfig();

        if (oAuth2Config == null) {
            throw new AuthenticationException("config must not be null");
        }
        // TODO check session ?

        OAuth2Util.checkOauthConfig(oAuth2Config);

        if (StringUtil.isEmpty(oAuth2Config.getResponseType())) {
            // TODO 异常类型：401
            throw new UnsupportedAuthenticationException(String.format("%s is not provided", OAuth2ParameterNames.RESPONSE_TYPE));
        }
        return chain.process(oAuth2RequestParameter, new OAuth2AuthenticationToken());
    }
}
