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

package org.lan.iti.iha.oauth2.token.support;

import cn.hutool.core.util.ArrayUtil;
import org.lan.iti.iha.oauth2.GrantType;
import org.lan.iti.iha.oauth2.OAuth2Config;
import org.lan.iti.iha.oauth2.OAuth2Constants;
import org.lan.iti.iha.oauth2.OAuth2ParameterNames;
import org.lan.iti.iha.oauth2.security.OAuth2RequestParameter;
import org.lan.iti.iha.oauth2.token.AccessToken;
import org.lan.iti.iha.oauth2.token.AccessTokenHelper;
import org.lan.iti.iha.oauth2.token.AccessTokenProvider;
import org.lan.iti.iha.oauth2.util.OAuth2Util;
import org.lan.iti.iha.security.exception.authentication.AuthenticationException;

import java.util.HashMap;
import java.util.Map;

/**
 * 4.3.  Resource Owner Password Credentials Grant
 *
 * @author NorthLan
 * @date 2021/8/4
 * @url https://blog.noahlan.com
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.3" target="_blank">4.3.  Resource Owner Password Credentials Grant</a>
 */
public class PasswordTokenProvider implements AccessTokenProvider {
    @Override
    public boolean matches(String params) {
        return GrantType.PASSWORD.getType().equals(params);
    }

    @Override
    public AccessToken getToken(OAuth2RequestParameter parameter, OAuth2Config oAuth2Config) throws AuthenticationException {
        Map<String, String> params = new HashMap<>(6);
        params.put(OAuth2ParameterNames.GRANT_TYPE, GrantType.PASSWORD.getType());
        params.put(OAuth2ParameterNames.USERNAME, parameter.getUsername());
        params.put(OAuth2ParameterNames.PASSWORD, parameter.getPassword());
        params.put(OAuth2ParameterNames.CLIENT_ID, oAuth2Config.getClientId());
        params.put(OAuth2ParameterNames.CLIENT_SECRET, oAuth2Config.getClientSecret());
        if (ArrayUtil.isNotEmpty(oAuth2Config.getScope())) {
            params.put(OAuth2ParameterNames.SCOPE, String.join(OAuth2Constants.SCOPE_SEPARATOR, oAuth2Config.getScope()));
        }
        Map<String, Object> tokenInfo = OAuth2Util.request(oAuth2Config.getAccessTokenEndpointMethodType(), oAuth2Config.getTokenUri(), params);
        OAuth2Util.checkOAuthResponse(tokenInfo, "failed to get AccessToken.");

        if (!tokenInfo.containsKey(OAuth2ParameterNames.ACCESS_TOKEN)) {
            throw new AuthenticationException("failed to get AccessToken. response: " + tokenInfo);
        }
        return AccessTokenHelper.toAccessToken(tokenInfo);
    }
}
