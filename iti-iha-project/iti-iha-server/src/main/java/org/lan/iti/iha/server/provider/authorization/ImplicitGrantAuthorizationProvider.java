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

package org.lan.iti.iha.server.provider.authorization;

import org.lan.iti.common.core.util.StringUtil;
import org.lan.iti.iha.oauth2.OAuth2ParameterNames;
import org.lan.iti.iha.oidc.OidcParameterNames;
import org.lan.iti.iha.security.userdetails.UserDetails;
import org.lan.iti.iha.server.model.AccessToken;
import org.lan.iti.iha.security.clientdetails.ClientDetails;
import org.lan.iti.iha.server.security.IhaServerRequestParam;
import org.lan.iti.iha.server.model.enums.ResponseType;
import org.lan.iti.iha.server.provider.AuthorizationProvider;
import org.lan.iti.iha.server.util.OAuth2Util;
import org.lan.iti.iha.server.util.TokenUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 4.2.  Implicit Grant
 *
 * @author NorthLan
 * @date 2021/8/2
 * @url https://blog.noahlan.com
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.2">4.2.  Implicit Grant</a>
 */
public class ImplicitGrantAuthorizationProvider implements AuthorizationProvider {
    @Override
    public boolean matches(String params) {
        return ResponseType.TOKEN.getType().equalsIgnoreCase(params);
    }

    @Override
    public String generateRedirect(IhaServerRequestParam param, String responseType, ClientDetails clientDetails, UserDetails userDetails, String issuer) {
        AccessToken accessToken = TokenUtil.createAccessToken(userDetails, clientDetails, param.getGrantType(), param.getScope(), param.getNonce(), issuer);
        Map<String, String> tokenResponse = new HashMap<>(9);
        // https://tools.ietf.org/html/rfc6749#section-4.2.2
        // The authorization server MUST NOT issue a refresh token.
        tokenResponse.put(OAuth2ParameterNames.ACCESS_TOKEN, accessToken.getAccessToken());
        tokenResponse.put(OAuth2ParameterNames.EXPIRES_IN, String.valueOf(OAuth2Util.getAccessTokenExpiresIn(clientDetails.getAccessTokenTimeToLive())));
        tokenResponse.put(OAuth2ParameterNames.TOKEN_TYPE, OAuth2ParameterNames.TOKEN_TYPE_BEARER);
        tokenResponse.put(OAuth2ParameterNames.SCOPE, param.getScope());
        if (OAuth2Util.isOidcProtocol(param.getScope())) {
            tokenResponse.put(OidcParameterNames.ID_TOKEN, TokenUtil.createIdToken(clientDetails, userDetails, param.getNonce(), issuer));
        }
        if (StringUtil.isNotEmpty(param.getState())) {
            tokenResponse.put(OAuth2ParameterNames.STATE, param.getState());
        }
        String params = StringUtil.parseMapToString(tokenResponse, false);
        return param.getRedirectUri() + "?" + params;
    }
}
