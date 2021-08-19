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

import org.lan.iti.iha.oauth2.GrantType;
import org.lan.iti.iha.oauth2.OAuth2Config;
import org.lan.iti.iha.oauth2.security.OAuth2RequestParameter;
import org.lan.iti.iha.oauth2.token.AccessToken;
import org.lan.iti.iha.oauth2.token.AccessTokenProvider;
import org.lan.iti.iha.security.exception.authentication.AuthenticationException;

/**
 * 4.4.  Client Credentials Grant
 *
 * @author NorthLan
 * @date 2021/8/4
 * @url https://blog.noahlan.com
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.4" target="_blank">4.4.  Client Credentials Grant</a>
 */
public class ClientDetailsTokenProvider implements AccessTokenProvider {
    @Override
    public boolean matches(String params) {
        return GrantType.CLIENT_CREDENTIALS.getType().equals(params);
    }

    @Override
    public AccessToken getToken(OAuth2RequestParameter parameter, OAuth2Config oAuth2Config) throws AuthenticationException {
        throw new AuthenticationException("failed to get AccessToken. Grant type of client_credentials type is not supported.");
        //        Map<String, String> params = Maps.newHashMap();
//        params.put("grant_type", Oauth2GrantType.client_credentials.name());
//        if (ArrayUtil.isNotEmpty(oAuthConfig.getScopes())) {
//            params.put("scope", String.join(Oauth2Const.SCOPE_SEPARATOR, oAuthConfig.getScopes()));
//        }
//
//        Kv tokenInfo = Oauth2Util.request(oAuthConfig.getAccessTokenEndpointMethodType(), oAuthConfig.getTokenUrl(), params);
//        Oauth2Util.checkOauthResponse(tokenInfo, "Oauth2Strategy failed to get AccessToken.");
//
//        if (ObjectUtil.isEmpty(request.getParameter("access_token"))) {
//            throw new IhaOAuth2Exception("Oauth2Strategy failed to get AccessToken." + tokenInfo.toString());
//        }
//
//        return mapToAccessToken(tokenInfo);
    }
}
