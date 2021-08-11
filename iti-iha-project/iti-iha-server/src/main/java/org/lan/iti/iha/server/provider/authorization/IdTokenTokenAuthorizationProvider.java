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

import org.lan.iti.iha.security.userdetails.UserDetails;
import org.lan.iti.iha.server.model.AccessToken;
import org.lan.iti.iha.security.clientdetails.ClientDetails;
import org.lan.iti.iha.server.security.IhaServerRequestParam;
import org.lan.iti.iha.server.model.enums.ResponseType;
import org.lan.iti.iha.server.provider.AuthorizationProvider;
import org.lan.iti.iha.server.util.TokenUtil;

/**
 * When the value of {@code response_type} is {@code id_token token}, the {@code id_token} and {@code access_token} are returned from the authorization endpoint.
 * This mode does not require the use of token endpoints.
 *
 * @author NorthLan
 * @date 2021/8/2
 * @url https://blog.noahlan.com
 * @see <a href="https://openid.net/specs/oauth-v2-multiple-response-types-1_0.html#Combinations">Definitions of Multiple-Valued Response Type Combinations</a>
 */
public class IdTokenTokenAuthorizationProvider implements AuthorizationProvider {
    @Override
    public boolean matches(String params) {
        return ResponseType.ID_TOKEN_TOKEN.getType().equalsIgnoreCase(params);
    }

    @Override
    public String generateRedirect(IhaServerRequestParam param, String responseType, ClientDetails clientDetails, UserDetails userDetails, String issuer) {
        AccessToken accessToken = TokenUtil.createAccessToken(userDetails, clientDetails, param.getGrantType(), param.getScope(), param.getNonce(), issuer);
        String params = "?access_token=" + accessToken.getAccessToken() + "&id_token=" + TokenUtil.createIdToken(clientDetails, userDetails, param.getNonce(), issuer);
        return param.getRedirectUri() + params;
    }
}
