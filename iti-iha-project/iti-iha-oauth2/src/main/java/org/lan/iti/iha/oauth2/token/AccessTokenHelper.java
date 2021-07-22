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

package org.lan.iti.iha.oauth2.token;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.xkcoding.json.util.Kv;
import lombok.experimental.UtilityClass;
import org.lan.iti.iha.core.exception.IhaOAuth2Exception;
import org.lan.iti.iha.oauth2.*;
import org.lan.iti.iha.oauth2.pkce.PkceHelper;
import org.lan.iti.iha.oauth2.pkce.PkceParams;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@UtilityClass
public class AccessTokenHelper {
    /**
     * get access_token
     *
     * @param request     Current callback request
     * @param oAuthConfig oauth config
     * @return AccessToken
     */
    public static AccessToken getToken(HttpServletRequest request, OAuthConfig oAuthConfig) throws IhaOAuth2Exception {
        if (null == oAuthConfig) {
            throw new IhaOAuth2Exception("OAuth2Strategy failed to get AccessToken. OAuthConfig cannot be empty");
        }
        if (StrUtil.equals(oAuthConfig.getResponseType(), OAuth2ResponseType.CODE)) {
            return getAccessTokenOfAuthorizationCodeMode(request, oAuthConfig);
        }
        if (StrUtil.equals(oAuthConfig.getResponseType(), OAuth2ResponseType.TOKEN)) {
            return getAccessTokenOfImplicitMode(request);
        }
        if (oAuthConfig.getGrantType() == GrantType.PASSWORD) {
            return getAccessTokenOfPasswordMode(oAuthConfig);
        }
        if (oAuthConfig.getGrantType() == GrantType.CLIENT_CREDENTIALS) {
            return getAccessTokenOfClientMode(request, oAuthConfig);
        }
        throw new IhaOAuth2Exception("Oauth2Strategy failed to get AccessToken. Missing required parameters");
    }


    /**
     * 4.1.  Authorization Code Grant
     *
     * @param request     current callback request
     * @param oAuthConfig oauth config
     * @return token request url
     * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.1" target="_blank">4.1.  Authorization Code Grant</a>
     */
    private static AccessToken getAccessTokenOfAuthorizationCodeMode(HttpServletRequest request, OAuthConfig oAuthConfig) throws IhaOAuth2Exception {
        String state = request.getParameter(OAuth2ParameterNames.STATE);
        OAuth2Util.checkState(state, oAuthConfig.getClientId(), oAuthConfig.isVerifyState());

        String code = request.getParameter(OAuth2ParameterNames.CODE);
        Map<String, String> params = new HashMap<>(6);
        params.put(OAuth2ParameterNames.GRANT_TYPE, GrantType.AUTHORIZATION_CODE.getType());
        params.put(OAuth2ParameterNames.CODE, code);
        params.put(OAuth2ParameterNames.CLIENT_ID, oAuthConfig.getClientId());
        params.put(OAuth2ParameterNames.CLIENT_SECRET, oAuthConfig.getClientSecret());
        if (StrUtil.isNotBlank(oAuthConfig.getCallbackUrl())) {
            params.put(OAuth2ParameterNames.REDIRECT_URI, oAuthConfig.getCallbackUrl());
        }
        // PKCE is only applicable to authorization code mode
        if (StrUtil.equals(oAuthConfig.getResponseType(), OAuth2ResponseType.CODE) && oAuthConfig.isRequireProofKey()) {
            params.put(PkceParams.CODE_VERIFIER, PkceHelper.getCacheCodeVerifier(oAuthConfig.getClientId()));
        }

        Kv tokenInfo = OAuth2Util.request(oAuthConfig.getAccessTokenEndpointMethodType(), oAuthConfig.getTokenUrl(), params);
        OAuth2Util.checkOauthResponse(tokenInfo, "Oauth2Strategy failed to get AccessToken.");

        if (!tokenInfo.containsKey(OAuth2ParameterNames.ACCESS_TOKEN)) {
            throw new IhaOAuth2Exception("Oauth2Strategy failed to get AccessToken." + tokenInfo);
        }

        return mapToAccessToken(tokenInfo);
    }

    /**
     * 4.2.  Implicit Grant
     *
     * @param request current callback request
     * @return token request url
     * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.2" target="_blank">4.2.  Implicit Grant</a>
     */
    private static AccessToken getAccessTokenOfImplicitMode(HttpServletRequest request) throws IhaOAuth2Exception {
        OAuth2Util.checkOauthCallbackRequest(request.getParameter("error"), request.getParameter("error_description"),
                "Oauth2Strategy failed to get AccessToken.");

        if (null == request.getParameter(OAuth2ParameterNames.ACCESS_TOKEN)) {
            throw new IhaOAuth2Exception("Oauth2Strategy failed to get AccessToken.");
        }
        return AccessToken.builder()
                .accessToken(request.getParameter(OAuth2ParameterNames.ACCESS_TOKEN))
                .refreshToken(request.getParameter(OAuth2ParameterNames.REFRESH_TOKEN))
                .idToken(request.getParameter(OAuth2ParameterNames.ID_TOKEN))
                .tokenType(request.getParameter(OAuth2ParameterNames.TOKEN_TYPE))
                .scope(request.getParameter(OAuth2ParameterNames.SCOPE))
                .expiresIn(Convert.toLong(request.getParameter(OAuth2ParameterNames.EXPIRES_IN)))
                .build();
    }

    /**
     * 4.3.  Resource Owner Password Credentials Grant
     *
     * @param oAuthConfig oauth config
     * @return token request url
     * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.3" target="_blank">4.3.  Resource Owner Password Credentials Grant</a>
     */
    private static AccessToken getAccessTokenOfPasswordMode(OAuthConfig oAuthConfig) throws IhaOAuth2Exception {
        Map<String, String> params = new HashMap<>(6);
        params.put(OAuth2ParameterNames.GRANT_TYPE, GrantType.PASSWORD.getType());
        params.put(OAuth2ParameterNames.USERNAME, oAuthConfig.getUsername());
        params.put(OAuth2ParameterNames.PASSWORD, oAuthConfig.getPassword());
        params.put(OAuth2ParameterNames.CLIENT_ID, oAuthConfig.getClientId());
        params.put(OAuth2ParameterNames.CLIENT_SECRET, oAuthConfig.getClientSecret());
        if (ArrayUtil.isNotEmpty(oAuthConfig.getScopes())) {
            params.put(OAuth2ParameterNames.SCOPE, String.join(OAuth2Constants.SCOPE_SEPARATOR, oAuthConfig.getScopes()));
        }
        Kv tokenInfo = OAuth2Util.request(oAuthConfig.getAccessTokenEndpointMethodType(), oAuthConfig.getTokenUrl(), params);
        OAuth2Util.checkOauthResponse(tokenInfo, "Oauth2Strategy failed to get AccessToken.");

        if (!tokenInfo.containsKey(OAuth2ParameterNames.ACCESS_TOKEN)) {
            throw new IhaOAuth2Exception("Oauth2Strategy failed to get AccessToken." + tokenInfo.toString());
        }
        return mapToAccessToken(tokenInfo);
    }

    /**
     * 4.4.  Client Credentials Grant
     *
     * @param oAuthConfig oauth config
     * @return token request url
     * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.4" target="_blank">4.4.  Client Credentials Grant</a>
     */
    private static AccessToken getAccessTokenOfClientMode(HttpServletRequest request, OAuthConfig oAuthConfig) throws IhaOAuth2Exception {
        throw new IhaOAuth2Exception("Oauth2Strategy failed to get AccessToken. Grant type of client_credentials type is not supported.");
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

    private static AccessToken mapToAccessToken(Kv tokenMap) {
        return AccessToken.builder()
                .accessToken(tokenMap.getString(OAuth2ParameterNames.ACCESS_TOKEN))
                .refreshToken(tokenMap.getString(OAuth2ParameterNames.REFRESH_TOKEN))
                // TODO AccessToken应该提供一种可扩展的方式 claims
                .idToken(tokenMap.getString(OAuth2ParameterNames.ID_TOKEN))
                .tokenType(tokenMap.getString(OAuth2ParameterNames.TOKEN_TYPE))
                .scope(tokenMap.getString(OAuth2ParameterNames.SCOPE))
                .expiresIn(tokenMap.getLong(OAuth2ParameterNames.EXPIRES_IN))
                .build();
    }
}
