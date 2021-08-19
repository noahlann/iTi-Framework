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

import org.lan.iti.iha.oauth2.GrantType;
import org.lan.iti.iha.security.IhaSecurity;
import org.lan.iti.iha.security.clientdetails.ClientDetails;
import org.lan.iti.iha.server.model.AuthorizationToken;
import org.lan.iti.iha.server.model.AuthorizationTokenHelper;
import org.lan.iti.iha.server.security.IhaServerRequestParam;
import org.lan.iti.iha.server.util.AuthorizationTokenUtil;
import org.lan.iti.iha.server.util.EndpointUtil;
import org.lan.iti.iha.server.util.OAuth2Util;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * RFC6749 4.4.  Client Credentials Grant
 *
 * @author NorthLan
 * @date 2021/8/2
 * @url https://blog.noahlan.com
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.4" target="_blank">4.4.  Client Credentials Grant</a>
 */
public class ClientCredentialsProcessor extends AbstractAuthorizationTokenProcessor {
    @Override
    public boolean matches(String params) {
        return GrantType.CLIENT_CREDENTIALS.getType().equals(params);
    }

    @Override
    protected Map<String, Object> processor(IhaServerRequestParam param) {
        HttpServletRequest request = param.getRequest();
        String clientId = param.getClientId();

        ClientDetails clientDetails = IhaSecurity.getContext().getClientDetailsService().getByClientId(clientId);
        String requestScope = param.getScope();

        OAuth2Util.validClientDetails(clientDetails);
        OAuth2Util.validateScope(requestScope, clientDetails.getScopes());
        OAuth2Util.validateGrantType(param.getGrantType(), clientDetails.getGrantTypes(), GrantType.CLIENT_CREDENTIALS);
        OAuth2Util.validateSecret(param, clientDetails);

        AuthorizationToken authorizationToken = AuthorizationTokenUtil.createClientCredentialsAccessToken(
                clientDetails,
                param.getGrantType(),
                requestScope,
                param.getNonce(),
                EndpointUtil.getIssuer(request));

        // https://tools.ietf.org/html/rfc6749#section-4.2.2
        // The authorization server MUST NOT issue a refresh token.
        authorizationToken.setRefreshToken(null)
                .setRefreshTokenExpiresIn(null)
                .setRefreshTokenExpiration(null);

        authorizationToken.setIdToken(null)
                .setIdTokenExpiresIn(null)
                .setIdTokenExpiration(null);

        return AuthorizationTokenHelper.toMap(authorizationToken);
    }
}