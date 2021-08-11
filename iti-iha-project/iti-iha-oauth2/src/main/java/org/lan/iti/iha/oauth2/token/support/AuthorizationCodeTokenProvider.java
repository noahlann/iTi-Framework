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

import cn.hutool.core.util.StrUtil;
import com.xkcoding.json.util.Kv;
import org.lan.iti.iha.oauth2.GrantType;
import org.lan.iti.iha.oauth2.OAuth2Config;
import org.lan.iti.iha.oauth2.OAuth2ParameterNames;
import org.lan.iti.iha.oauth2.OAuth2ResponseType;
import org.lan.iti.iha.oauth2.pkce.PkceHelper;
import org.lan.iti.iha.oauth2.pkce.PkceParams;
import org.lan.iti.iha.oauth2.security.OAuth2RequestParameter;
import org.lan.iti.iha.oauth2.token.AccessToken;
import org.lan.iti.iha.oauth2.token.AccessTokenProvider;
import org.lan.iti.iha.oauth2.util.OAuth2Util;
import org.lan.iti.iha.security.exception.AuthenticationException;

import java.util.HashMap;
import java.util.Map;

/**
 * 4.1.  Authorization Code Grant
 *
 * @author NorthLan
 * @date 2021/8/4
 * @url https://blog.noahlan.com
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.1" target="_blank">4.1.  Authorization Code Grant</a>
 */
public class AuthorizationCodeTokenProvider implements AccessTokenProvider {
    @Override
    public boolean matches(String params) {
        return OAuth2ResponseType.CODE.equals(params);
    }

    @Override
    public AccessToken getToken(OAuth2RequestParameter parameter, OAuth2Config oAuth2Config) throws AuthenticationException {
        String state = parameter.getState();
        OAuth2Util.checkState(state, oAuth2Config.getClientId(), oAuth2Config.isVerifyState());

        String code = parameter.getCode();
        Map<String, String> params = new HashMap<>(6);
        params.put(OAuth2ParameterNames.GRANT_TYPE, GrantType.AUTHORIZATION_CODE.getType());
        params.put(OAuth2ParameterNames.CODE, code);
        params.put(OAuth2ParameterNames.CLIENT_ID, oAuth2Config.getClientId());
        params.put(OAuth2ParameterNames.CLIENT_SECRET, oAuth2Config.getClientSecret());
        if (StrUtil.isNotBlank(oAuth2Config.getCallbackUrl())) {
            params.put(OAuth2ParameterNames.REDIRECT_URI, oAuth2Config.getCallbackUrl());
        }
        // PKCE is only applicable to authorization code mode
        if (StrUtil.equals(oAuth2Config.getResponseType(), OAuth2ResponseType.CODE) && oAuth2Config.isRequireProofKey()) {
            params.put(PkceParams.CODE_VERIFIER, PkceHelper.getCacheCodeVerifier(oAuth2Config.getClientId()));
        }

        Kv tokenInfo = OAuth2Util.request(oAuth2Config.getAccessTokenEndpointMethodType(), oAuth2Config.getTokenUrl(), params);
        OAuth2Util.checkOauthResponse(tokenInfo, "failed to get AccessToken.");

        if (!tokenInfo.containsKey(OAuth2ParameterNames.ACCESS_TOKEN)) {
            throw new AuthenticationException("failed to get AccessToken." + tokenInfo);
        }

        return mapToAccessToken(tokenInfo);
    }
}
