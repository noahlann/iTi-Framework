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
import org.lan.iti.iha.server.IhaServer;
import org.lan.iti.iha.server.exception.IhaServerException;
import org.lan.iti.iha.server.model.AccessToken;
import org.lan.iti.iha.server.security.IhaServerRequestParam;
import org.lan.iti.iha.server.model.enums.ErrorResponse;
import org.lan.iti.iha.server.provider.AuthorizationTokenProvider;
import org.lan.iti.iha.server.util.EndpointUtil;
import org.lan.iti.iha.server.util.OAuth2Util;
import org.lan.iti.iha.server.util.TokenUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * RFC6749 4.3.  Resource Owner Password Credentials Grant
 *
 * @author NorthLan
 * @date 2021/8/2
 * @url https://blog.noahlan.com
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.3" target="_blank">4.3.  Resource Owner Password Credentials Grant</a>
 */
public class ResourceOwnerPasswordProvider implements AuthorizationTokenProvider {
    @Override
    public boolean matches(String params) {
        return GrantType.PASSWORD.getType().equals(params);
    }

    @Override
    public IhaResponse generateResponse(IhaServerRequestParam param) {
        HttpServletRequest request = param.getRequest();

        String username = param.getUsername();
        String password = param.getPassword();
        String clientId = param.getClientId();
        UserDetails userDetails = IhaSecurity.getContext().getUserDetailsService().loadByType(username,
                GrantType.PASSWORD.getType(),
                clientId);
        if (null == userDetails) {
            throw new IhaServerException(ErrorResponse.INVALID_USER_CERTIFICATE);
        }
        IhaSecurity.getContext().getAuthenticationChecker().checkByCredentials(userDetails, password);

        IhaServer.saveUser(userDetails, request);

        ClientDetails clientDetails = IhaSecurity.getContext().getClientDetailsService().getByClientId(param.getClientId());
        String requestScope = param.getScope();

        OAuth2Util.validClientDetail(clientDetails);
        OAuth2Util.validateScope(requestScope, clientDetails.getScopes());
        OAuth2Util.validateGrantType(param.getGrantType(), clientDetails.getGrantTypes(), GrantType.PASSWORD);
        OAuth2Util.validateSecret(param, clientDetails);

        long expiresIn = OAuth2Util.getAccessTokenExpiresIn(clientDetails.getAccessTokenTimeToLive());

        AccessToken accessToken = TokenUtil.createAccessToken(userDetails, clientDetails, param.getGrantType(), requestScope, param.getNonce(), EndpointUtil.getIssuer(request));
        IhaResponse response = IhaResponse.empty()
                .put(OAuth2ParameterNames.ACCESS_TOKEN, accessToken.getAccessToken())
                .put(OAuth2ParameterNames.REFRESH_TOKEN, accessToken.getRefreshToken())
                .put(OAuth2ParameterNames.EXPIRES_IN, expiresIn)
                .put(OAuth2ParameterNames.TOKEN_TYPE, OAuth2ParameterNames.TOKEN_TYPE_BEARER)
                .put(OAuth2ParameterNames.SCOPE, requestScope);

        if (OAuth2Util.isOidcProtocol(requestScope)) {
            response.put(OidcParameterNames.ID_TOKEN, TokenUtil.createIdToken(clientDetails, userDetails, param.getNonce(), EndpointUtil.getIssuer(request)));
        }
        return response;
    }
}