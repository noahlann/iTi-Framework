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

import org.lan.iti.iha.security.clientdetails.ClientDetails;
import org.lan.iti.iha.security.userdetails.UserDetails;
import org.lan.iti.iha.server.model.AuthorizationToken;
import org.lan.iti.iha.server.model.AuthorizationTokenHelper;
import org.lan.iti.iha.server.model.enums.ResponseType;
import org.lan.iti.iha.server.security.IhaServerRequestParam;
import org.lan.iti.iha.server.util.AuthorizationTokenUtil;

import java.util.Map;

/**
 * 4.2.  Implicit Grant (response_type = token)
 *
 * @author NorthLan
 * @date 2021/8/2
 * @url https://blog.noahlan.com
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.2">4.2.  Implicit Grant</a>
 */
public class TokenAuthorizationProvider extends AbstractAuthorizationProvider {
    @Override
    public boolean matches(String params) {
        return ResponseType.TOKEN.getType().equals(params);
    }

    @Override
    protected void process(Map<String, Object> result, IhaServerRequestParam param, String responseType, ClientDetails clientDetails, UserDetails userDetails, String issuer) {
        AuthorizationToken authorizationToken = AuthorizationTokenUtil.createAccessToken(
                userDetails,
                clientDetails,
                param.getGrantType(),
                param.getScope(),
                param.getNonce(),
                issuer,
                false);
        // remove idToken
        authorizationToken.setIdToken(null)
                .setIdTokenExpiresIn(null)
                .setIdTokenExpiration(null);
        // remove refreshToken
        // https://tools.ietf.org/html/rfc6749#section-4.2.2
        // The authorization server MUST NOT issue a refresh token.
        authorizationToken.setRefreshToken(null)
                .setRefreshTokenExpiration(null)
                .setRefreshTokenExpiresIn(null);
        // remove others
        authorizationToken.setClientId(null)
                .setGrantType(null);

        // TODO 缓存还是不缓存，这是一个问题

        Map<String, Object> tokenMap = AuthorizationTokenHelper.toMap(authorizationToken);
        result.putAll(tokenMap);
    }
}
