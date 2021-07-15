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

package org.lan.iti.cloud.iha.server.provider;

import lombok.AllArgsConstructor;
import org.lan.iti.cloud.iha.oauth2.OAuth2ParameterNames;
import org.lan.iti.cloud.iha.oidc.OidcParameterNames;
import org.lan.iti.cloud.iha.server.IhaServerConstants;
import org.lan.iti.cloud.iha.server.model.AccessToken;
import org.lan.iti.cloud.iha.server.model.ClientDetails;
import org.lan.iti.cloud.iha.server.model.IhaServerRequestParam;
import org.lan.iti.cloud.iha.server.model.UserDetails;
import org.lan.iti.cloud.iha.server.service.OAuth2Service;
import org.lan.iti.cloud.iha.server.util.OAuthUtil;
import org.lan.iti.cloud.iha.server.util.StringUtil;
import org.lan.iti.cloud.iha.server.util.TokenUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Authorize the endpoint to create a callback url, and pass different callback parameters according to the request parameters
 *
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@AllArgsConstructor
public class AuthorizationProvider {
    private final OAuth2Service oAuth2Service;

    /**
     * 4.2.  Implicit Grant
     *
     * @param userDetails          Logged-in user information
     * @param param         Request parameter
     * @param clientDetails Application information
     * @param issuer        The issuer name. This parameter cannot contain the colon (:) character.
     * @return String
     * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.2">4.2.  Implicit Grant</a>
     */
    public String generateImplicitGrantResponse(UserDetails userDetails, IhaServerRequestParam param, ClientDetails clientDetails, String issuer) {
        AccessToken accessToken = TokenUtil.createAccessToken(userDetails, clientDetails, param.getGrantType(), param.getScope(), param.getNonce(), issuer);
        Map<String, String> tokenResponse = new HashMap<>(9);
        // https://tools.ietf.org/html/rfc6749#section-4.2.2
        // The authorization server MUST NOT issue a refresh token.
        tokenResponse.put(OAuth2ParameterNames.ACCESS_TOKEN, accessToken.getAccessToken());
        tokenResponse.put(OAuth2ParameterNames.EXPIRES_IN, String.valueOf(OAuthUtil.getAccessTokenExpiresIn(clientDetails.getAccessTokenTimeToLive())));
        tokenResponse.put(OAuth2ParameterNames.TOKEN_TYPE, IhaServerConstants.TOKEN_TYPE_BEARER);
        tokenResponse.put(OAuth2ParameterNames.SCOPE, param.getScope());
        if (OAuthUtil.isOidcProtocol(param.getScope())) {
            tokenResponse.put(OidcParameterNames.ID_TOKEN, TokenUtil.createIdToken(clientDetails, userDetails, param.getNonce(), issuer));
        }
        if (StringUtil.isNotEmpty(param.getState())) {
            tokenResponse.put(OAuth2ParameterNames.STATE, param.getState());
        }
        String params = StringUtil.parseMapToString(tokenResponse, false);
        return param.getRedirectUri() + "?" + params;
    }

    /**
     * 4.1.  Authorization Code Grant
     *
     * @param userDetails          Logged-in user information
     * @param param         Request parameter
     * @param clientDetails Application information
     * @return String
     * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.1">4.1.  Authorization Code Grant</a>
     */
    public String generateAuthorizationCodeResponse(UserDetails userDetails, IhaServerRequestParam param, ClientDetails clientDetails) {
        String authorizationCode = oAuth2Service.createAuthorizationCode(param, userDetails, OAuthUtil.getCodeExpiresIn(clientDetails.getCodeTimeToLive()));
        String params = "?code=" + authorizationCode;
        if (StringUtil.isNotEmpty(param.getState())) {
            params = params + "&state=" + param.getState();
        }
        return param.getRedirectUri() + params;
    }

    /**
     * When the value of {@code response_type} is {@code code id_token}, return {@code code} and {@code id_token} from the authorization endpoint
     *
     * @param userDetails          Logged-in user information
     * @param param         Request parameter
     * @param clientDetails Application information
     * @param issuer        The issuer name. This parameter cannot contain the colon (:) character.
     * @return String
     * @see <a href="https://openid.net/specs/oauth-v2-multiple-response-types-1_0.html#Combinations">Definitions of Multiple-Valued Response Type Combinations</a>
     */
    public String generateCodeIdTokenAuthorizationResponse(UserDetails userDetails, IhaServerRequestParam param, ClientDetails clientDetails, String issuer) {
        String params = "&id_token=" + TokenUtil.createIdToken(clientDetails, userDetails, param.getNonce(), issuer);
        return this.generateAuthorizationCodeResponse(userDetails, param, clientDetails) + params;
    }

    /**
     * When the value of {@code response_type} is {@code id_token}, an {@code id_token} is returned from the authorization endpoint.
     * This mode does not require the use of token endpoints.
     *
     * @param userDetails          Logged-in user information
     * @param param         Request parameter
     * @param clientDetails Application information
     * @param issuer        The issuer name. This parameter cannot contain the colon (:) character.
     * @return String
     */
    public String generateIdTokenAuthorizationResponse(UserDetails userDetails, IhaServerRequestParam param, ClientDetails clientDetails, String issuer) {
        String params = "?id_token=" + TokenUtil.createIdToken(clientDetails, userDetails, param, issuer);
        return param.getRedirectUri() + params;
    }

    /**
     * When the value of {@code response_type} is {@code id_token token}, the {@code id_token} and {@code access_token} are returned from the authorization endpoint.
     * This mode does not require the use of token endpoints.
     *
     * @param userDetails          Logged-in user information
     * @param param         Request parameter
     * @param clientDetails Application information
     * @param issuer        The issuer name. This parameter cannot contain the colon (:) character.
     * @return String
     * @see <a href="https://openid.net/specs/oauth-v2-multiple-response-types-1_0.html#Combinations">Definitions of Multiple-Valued Response Type Combinations</a>
     */
    public String generateIdTokenTokenAuthorizationResponse(UserDetails userDetails, IhaServerRequestParam param, ClientDetails clientDetails, String issuer) {
        AccessToken accessToken = TokenUtil.createAccessToken(userDetails, clientDetails, param.getGrantType(), param.getScope(), param.getNonce(), issuer);
        String params = "?access_token=" + accessToken.getAccessToken() + "&id_token=" + TokenUtil.createIdToken(clientDetails, userDetails, param.getNonce(), issuer);
        return param.getRedirectUri() + params;
    }

    /**
     * When the value of {@code response_type} is {@code code token}, return {@code code} and {@code token} from the authorization endpoint
     *
     * @param userDetails          Logged-in user information
     * @param param         Request parameter
     * @param clientDetails Application information
     * @param issuer        The issuer name. This parameter cannot contain the colon (:) character.
     * @return String
     * @see <a href="https://openid.net/specs/oauth-v2-multiple-response-types-1_0.html#Combinations">Definitions of Multiple-Valued Response Type Combinations</a>
     */
    public String generateCodeTokenAuthorizationResponse(UserDetails userDetails, IhaServerRequestParam param, ClientDetails clientDetails, String issuer) {
        AccessToken accessToken = TokenUtil.createAccessToken(userDetails, clientDetails, param.getGrantType(), param.getScope(), param.getNonce(), issuer);
        String params = "&access_token=" + accessToken.getAccessToken();
        return this.generateAuthorizationCodeResponse(userDetails, param, clientDetails) + params;
    }

    /**
     * When the value of {@code response_type} is {@code code id_token token}, return {@code code},{@code id_token} and {@code token} from the authorization endpoint
     *
     * @param userDetails          Logged-in user information
     * @param param         Request parameter
     * @param clientDetails Application information
     * @param issuer        The issuer name. This parameter cannot contain the colon (:) character.
     * @return String
     * @see <a href="https://openid.net/specs/oauth-v2-multiple-response-types-1_0.html#Combinations">Definitions of Multiple-Valued Response Type Combinations</a>
     */
    public String generateCodeIdTokenTokenAuthorizationResponse(UserDetails userDetails, IhaServerRequestParam param, ClientDetails clientDetails, String issuer) {
        String params = "&id_token=" + TokenUtil.createIdToken(clientDetails, userDetails, param.getNonce(), issuer);
        return this.generateCodeTokenAuthorizationResponse(userDetails, param, clientDetails, issuer) + params;
    }

    /**
     * When the value of {@code response_type} is {@code none}, the {@code code},{@code id_token} and {@code token} is not returned from the authorization endpoint,
     * if there is a state, it is returned as it is.
     *
     * @param param Request parameter
     * @return String
     * @see <a href="https://openid.net/specs/oauth-v2-multiple-response-types-1_0.html#none">None Response Type</a>
     */
    public String generateNoneAuthorizationResponse(IhaServerRequestParam param) {
        String params = "";
        if (!StringUtil.isEmpty(param.getState())) {
            params = "?state=" + param.getState();
        }
        return param.getRedirectUri() + params;
    }
}
