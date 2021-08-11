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

import org.lan.iti.common.core.util.StringUtil;
import org.lan.iti.iha.core.result.IhaResponse;
import org.lan.iti.iha.oauth2.GrantType;
import org.lan.iti.iha.oauth2.OAuth2ParameterNames;
import org.lan.iti.iha.security.IhaSecurity;
import org.lan.iti.iha.security.clientdetails.ClientDetails;
import org.lan.iti.iha.server.model.AccessToken;
import org.lan.iti.iha.server.security.IhaServerRequestParam;
import org.lan.iti.iha.server.provider.AuthorizationTokenProvider;
import org.lan.iti.iha.server.util.EndpointUtil;
import org.lan.iti.iha.server.util.OAuth2Util;
import org.lan.iti.iha.server.util.TokenUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * RFC6749 4.4.  Client Credentials Grant
 *
 * @author NorthLan
 * @date 2021/8/2
 * @url https://blog.noahlan.com
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.4" target="_blank">4.4.  Client Credentials Grant</a>
 */
public class ClientCredentialsProvider implements AuthorizationTokenProvider {
    @Override
    public boolean matches(String params) {
        return GrantType.CLIENT_CREDENTIALS.getType().equals(params);
    }

    @Override
    public IhaResponse generateResponse(IhaServerRequestParam param) {
        HttpServletRequest request = param.getRequest();
        String clientId = param.getClientId();

        ClientDetails clientDetails = IhaSecurity.getContext().getClientDetailsService().getByClientId(clientId);
        String requestScope = param.getScope();

        OAuth2Util.validClientDetail(clientDetails);
        OAuth2Util.validateScope(requestScope, clientDetails.getScopes());
        OAuth2Util.validateGrantType(param.getGrantType(), clientDetails.getGrantTypes(), GrantType.CLIENT_CREDENTIALS);
        OAuth2Util.validateSecret(param, clientDetails);

        long expiresIn = OAuth2Util.getAccessTokenExpiresIn(clientDetails.getAccessTokenTimeToLive());

        AccessToken accessToken = TokenUtil.createClientCredentialsAccessToken(clientDetails, param.getGrantType(), requestScope, param.getNonce(), EndpointUtil.getIssuer(request));

        // https://tools.ietf.org/html/rfc6749#section-4.2.2
        // The authorization server MUST NOT issue a refresh token.
        IhaResponse response = IhaResponse.empty()
                .put(OAuth2ParameterNames.ACCESS_TOKEN, accessToken.getAccessToken())
                .put(OAuth2ParameterNames.EXPIRES_IN, expiresIn)
                .put(OAuth2ParameterNames.TOKEN_TYPE, OAuth2ParameterNames.TOKEN_TYPE_BEARER);
        if (!StringUtil.isEmpty(requestScope)) {
            response.put(OAuth2ParameterNames.SCOPE, requestScope);
        }
        return response;
    }
}