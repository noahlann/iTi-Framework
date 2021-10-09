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

import org.lan.iti.iha.oauth2.OAuth2Config;
import org.lan.iti.iha.oauth2.util.OAuth2Util;
import org.lan.iti.iha.security.authentication.Authentication;
import org.lan.iti.iha.security.exception.authentication.AuthenticationException;
import org.lan.iti.iha.security.mgt.RequestParameter;
import org.lan.iti.iha.security.processor.AuthenticationProcessor;
import org.lan.iti.iha.security.processor.ProcessChain;
import org.lan.iti.iha.security.processor.ProcessorType;

import java.util.HashMap;
import java.util.Map;

/**
 * 回收 Token
 *
 * @author NorthLan
 * @date 2021/8/14
 * @url https://blog.noahlan.com
 */
public class OAuth2RevokeTokenAuthenticationProcessor implements AuthenticationProcessor {

    @Override
    public boolean matches(String params) {
        return ProcessorType.OAUTH2_REVOKE.matches(params);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public boolean support(RequestParameter parameter, Authentication authentication) throws AuthenticationException {
        return parameter instanceof OAuth2RequestParameter &&
                authentication instanceof OAuth2AuthenticationToken &&
                ((OAuth2AuthenticationToken) authentication).isNeedRevoke();
    }

    @Override
    public Authentication process(RequestParameter parameter, Authentication authentication, ProcessChain chain) throws AuthenticationException {
        OAuth2RequestParameter oAuth2RequestParameter = (OAuth2RequestParameter) parameter;
        OAuth2Config config = oAuth2RequestParameter.getConfig();
        Map<String, String> params = new HashMap<>(6);
        params.put("access_token", oAuth2RequestParameter.getAccessToken());

        Map<String, Object> tokenInfo = OAuth2Util.request(config.getRevokeTokenEndpointMethodType(), config.getRevokeTokenUri(), params);

        OAuth2Util.checkOAuthResponse(tokenInfo, "failed to revoke access_token. " + oAuth2RequestParameter.getAccessToken());
        return new OAuth2AuthenticationToken(true);
    }
}
