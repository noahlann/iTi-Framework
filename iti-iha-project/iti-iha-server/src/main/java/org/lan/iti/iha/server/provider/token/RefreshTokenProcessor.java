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
import org.lan.iti.iha.security.userdetails.UserDetails;
import org.lan.iti.iha.server.model.AuthorizationToken;
import org.lan.iti.iha.server.model.AuthorizationTokenHelper;
import org.lan.iti.iha.server.security.IhaServerRequestParam;
import org.lan.iti.iha.server.util.AuthorizationTokenUtil;
import org.lan.iti.iha.server.util.EndpointUtil;
import org.lan.iti.iha.server.util.OAuth2Util;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * RFC6749 6.  Refreshing an AccessToken
 *
 * @author NorthLan
 * @date 2021/8/2
 * @url https://blog.noahlan.com
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-6" target="_blank">6.  Refreshing an Access Token</a>
 */
public class RefreshTokenProcessor extends AbstractAuthorizationTokenProcessor {
    @Override
    public boolean matches(String params) {
        return GrantType.REFRESH_TOKEN.getType().equals(params);
    }

    @Override
    protected Map<String, Object> processor(IhaServerRequestParam param) {
        HttpServletRequest request = param.getRequest();
        AuthorizationTokenUtil.validateRefreshToken(param.getRefreshToken());

        AuthorizationToken token = AuthorizationTokenUtil.getByRefreshToken(param.getRefreshToken());
        ClientDetails clientDetails = IhaSecurity.getContext().getClientDetailsService().getByClientId(token.getClientId());
        String requestScope = param.getScope();

        OAuth2Util.validClientDetails(clientDetails);
        OAuth2Util.validateScope(requestScope, clientDetails.getScopes());
        OAuth2Util.validateGrantType(param.getGrantType(), clientDetails.getGrantTypes(), GrantType.REFRESH_TOKEN);
        OAuth2Util.validateSecret(param, clientDetails);

        UserDetails userDetails = IhaSecurity.getContext().getUserDetailsService().loadById(token.getUserId());

        AuthorizationToken authorizationToken = AuthorizationTokenUtil.refreshAccessToken(
                userDetails,
                clientDetails,
                token,
                param.getNonce(),
                EndpointUtil.getIssuer(request));

        return AuthorizationTokenHelper.toMap(authorizationToken);
    }
}