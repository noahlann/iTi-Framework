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
import org.lan.iti.common.core.util.StringPool;
import org.lan.iti.common.core.util.StringUtil;
import org.lan.iti.iha.oauth2.GrantType;
import org.lan.iti.iha.oauth2.OAuth2Config;
import org.lan.iti.iha.oauth2.OAuth2ParameterNames;
import org.lan.iti.iha.oauth2.exception.InvalidTokenException;
import org.lan.iti.iha.oauth2.security.OAuth2RequestParameter;
import org.lan.iti.iha.oauth2.token.AccessToken;
import org.lan.iti.iha.oauth2.token.AccessTokenHelper;
import org.lan.iti.iha.oauth2.token.AccessTokenProvider;
import org.lan.iti.iha.oauth2.util.OAuth2Util;
import org.lan.iti.iha.security.exception.authentication.AuthenticationException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NorthLan
 * @date 2021/8/14
 * @url https://blog.noahlan.com
 */
public class RefreshTokenProvider implements AccessTokenProvider {
    @Override
    public boolean matches(String params) {
        return GrantType.REFRESH_TOKEN.getType().equals(params);
    }

    @Override
    public AccessToken getToken(OAuth2RequestParameter parameter, OAuth2Config oAuth2Config) throws AuthenticationException {
        if (StringUtil.isEmpty(parameter.getRefreshToken())) {
            throw new InvalidTokenException();
        }
        Map<String, String> params = new HashMap<>(6);
        params.put("grant_type", oAuth2Config.getGrantType().name());
        params.put("refresh_token", parameter.getRefreshToken());

        if (ArrayUtil.isNotEmpty(oAuth2Config.getScopes())) {
            params.put("scope", String.join(StringPool.SPACE, oAuth2Config.getScopes()));
        }

        Map<String, Object> tokenInfo = OAuth2Util.request(oAuth2Config.getAccessTokenEndpointMethodType(), oAuth2Config.getRefreshTokenUrl(), params);

        OAuth2Util.checkOAuthResponse(tokenInfo, "failed to refresh access_token.");
        if (!tokenInfo.containsKey(OAuth2ParameterNames.ACCESS_TOKEN)) {
            throw new AuthenticationException("failed to refresh access_token." + tokenInfo);
        }
        return AccessTokenHelper.toAccessToken(tokenInfo);
    }
}
