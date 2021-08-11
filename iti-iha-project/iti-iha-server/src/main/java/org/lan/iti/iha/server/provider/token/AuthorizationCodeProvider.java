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

package org.lan.iti.iha.server.provider.token;

import org.lan.iti.iha.core.result.IhaResponse;
import org.lan.iti.iha.oauth2.GrantType;
import org.lan.iti.iha.oauth2.OAuth2ParameterNames;
import org.lan.iti.iha.oidc.OidcParameterNames;
import org.lan.iti.iha.security.IhaSecurity;
import org.lan.iti.iha.security.clientdetails.ClientDetails;
import org.lan.iti.iha.security.userdetails.UserDetails;
import org.lan.iti.iha.server.model.AccessToken;
import org.lan.iti.iha.server.model.AuthorizationCode;
import org.lan.iti.iha.server.security.IhaServerRequestParam;
import org.lan.iti.iha.server.provider.AuthorizationTokenProvider;
import org.lan.iti.iha.server.util.EndpointUtil;
import org.lan.iti.iha.server.util.OAuth2Util;
import org.lan.iti.iha.server.util.TokenUtil;

/**
 * RFC6749 4.1. authorization code grant
 *
 * @author NorthLan
 * @date 2021/8/2
 * @url https://blog.noahlan.com
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.1" target="_blank">4.1.  Authorization Code Grant</a>
 */
public class AuthorizationCodeProvider implements AuthorizationTokenProvider {
    @Override
    public boolean matches(String params) {
        return GrantType.AUTHORIZATION_CODE.getType().equals(params);
    }

    @Override
    public IhaResponse generateResponse(IhaServerRequestParam param) {
        AuthorizationCode codeInfo = OAuth2Util.validateAndGetAuthorizationCode(param.getGrantType(), param.getCode());

        String scope = codeInfo.getScope();
        UserDetails userDetails = codeInfo.getUserDetails();
        String nonce = codeInfo.getNonce();

        ClientDetails clientDetails = IhaSecurity.getContext().getClientDetailsService().getByClientId(param.getClientId());

        OAuth2Util.validClientDetail(clientDetails);
        OAuth2Util.validateGrantType(param.getGrantType(), clientDetails.getGrantTypes(), GrantType.AUTHORIZATION_CODE);
        OAuth2Util.validateSecret(param, clientDetails);
        OAuth2Util.validateRedirectUri(param.getRedirectUri(), clientDetails);

        OAuth2Util.invalidateCode(param.getCode());

        long expiresIn = OAuth2Util.getAccessTokenExpiresIn(clientDetails.getAccessTokenTimeToLive());

        AccessToken accessToken = TokenUtil.createAccessToken(userDetails, clientDetails, param.getGrantType(), scope, nonce, EndpointUtil.getIssuer(param.getRequest()));
        IhaResponse response = IhaResponse.empty()
                .put(OAuth2ParameterNames.ACCESS_TOKEN, accessToken.getAccessToken())
                .put(OAuth2ParameterNames.REFRESH_TOKEN, accessToken.getRefreshToken())
                .put(OAuth2ParameterNames.EXPIRES_IN, expiresIn)
                .put(OAuth2ParameterNames.TOKEN_TYPE, OAuth2ParameterNames.TOKEN_TYPE_BEARER)
                .put(OAuth2ParameterNames.SCOPE, scope);
        if (OAuth2Util.isOidcProtocol(scope)) {
            response.put(OidcParameterNames.ID_TOKEN, TokenUtil.createIdToken(clientDetails, userDetails, nonce, EndpointUtil.getIssuer(param.getRequest())));
        }
        return response;
    }
}
