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

package org.lan.iti.iha.server.util;

import cn.hutool.crypto.SecureUtil;
import lombok.experimental.UtilityClass;
import org.lan.iti.common.core.util.StringUtil;
import org.lan.iti.iha.core.util.RequestUtil;
import org.lan.iti.iha.oauth2.OAuth2ParameterNames;
import org.lan.iti.iha.oauth2.token.BearerToken;
import org.lan.iti.iha.security.IhaSecurity;
import org.lan.iti.iha.security.clientdetails.ClientDetails;
import org.lan.iti.iha.security.userdetails.UserDetails;
import org.lan.iti.iha.server.IhaServerConstants;
import org.lan.iti.iha.server.exception.InvalidTokenException;
import org.lan.iti.iha.server.model.AccessToken;
import org.lan.iti.iha.server.model.enums.ErrorResponse;
import org.lan.iti.iha.server.security.IhaServerRequestParam;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@UtilityClass
public class TokenUtil {

    /**
     * Get access token from request
     *
     * @param request request
     * @return String
     */
    public static String getAccessToken(HttpServletRequest request) {
        String accessToken = getAccessTokenFromUrl(request);
        if (StringUtil.isEmpty(accessToken)) {
            accessToken = getAccessTokenFromHeader(request);
            if (StringUtil.isEmpty(accessToken)) {
                accessToken = getAccessTokenFromCookie(request);
            }
        }
        return accessToken;
    }

    private static String getAccessTokenFromUrl(HttpServletRequest request) {
        String accessToken = RequestUtil.getParam(OAuth2ParameterNames.ACCESS_TOKEN, request);
        if (StringUtil.isNotEmpty(accessToken)) {
            return accessToken;
        }
        return null;
    }

    private static String getAccessTokenFromHeader(HttpServletRequest request) {
        String accessToken = RequestUtil.getHeader(OAuth2ParameterNames.AUTHORIZATION_HEADER_NAME, request);
        return BearerToken.parse(accessToken);
    }

    private static String getAccessTokenFromCookie(HttpServletRequest request) {
        return RequestUtil.getCookieVal(request, OAuth2ParameterNames.ACCESS_TOKEN);
    }

    public static String createIdToken(ClientDetails clientDetails, UserDetails userDetails, String nonce, String issuer) {
        long idTokenExpiresIn = OAuth2Util.getIdTokenExpiresIn(clientDetails.getIdTokenTimeToLive());
        return JwtUtil.createJwtToken(clientDetails.getClientId(), userDetails, idTokenExpiresIn, nonce, issuer);
    }

    public static String createIdToken(ClientDetails clientDetails, UserDetails userDetails, IhaServerRequestParam param, String issuer) {
        long idTokenExpiresIn = OAuth2Util.getIdTokenExpiresIn(clientDetails.getIdTokenTimeToLive());
        return JwtUtil.createJwtToken(clientDetails.getClientId(), userDetails, idTokenExpiresIn, param.getNonce(), StringUtil.split(param.getScope(), IhaServerConstants.SPACE), param.getResponseType(), issuer);
    }

    public static AccessToken createAccessToken(UserDetails userDetails, ClientDetails clientDetails, String grantType, String scope, String nonce, String issuer) {
        String clientId = clientDetails.getClientId();

        long accessTokenExpiresIn = OAuth2Util.getAccessTokenExpiresIn(clientDetails.getAccessTokenTimeToLive());
        long refreshTokenExpiresIn = OAuth2Util.getRefreshTokenExpiresIn(clientDetails.getRefreshTokenTimeToLive());

        String accessTokenStr = JwtUtil.createJwtToken(clientId, userDetails, accessTokenExpiresIn, nonce, issuer);
        String refreshTokenStr = SecureUtil.sha256(clientId.concat(scope).concat(System.currentTimeMillis() + ""));

        AccessToken accessToken = new AccessToken();
        accessToken.setAccessToken(accessTokenStr);
        accessToken.setRefreshToken(refreshTokenStr);
        accessToken.setGrantType(grantType);
        if (null != userDetails) {
            accessToken.setUserName(userDetails.getUsername());
            accessToken.setUserId(userDetails.getId());
        }
        accessToken.setClientId(clientId);
        accessToken.setScope(scope);

        accessToken.setRefreshTokenExpiresIn(refreshTokenExpiresIn);
        accessToken.setAccessTokenExpiresIn(accessTokenExpiresIn);

        accessToken.setAccessTokenExpiration(OAuth2Util.getAccessTokenExpiresAt(clientDetails.getAccessTokenTimeToLive()));
        accessToken.setRefreshTokenExpiration(OAuth2Util.getRefreshTokenExpiresAt(clientDetails.getRefreshTokenTimeToLive()));

        String token = IhaServerConstants.OAUTH_ACCESS_TOKEN_CACHE_KEY + accessTokenStr;
        String refreshToken = IhaServerConstants.OAUTH_REFRESH_TOKEN_CACHE_KEY + refreshTokenStr;
        IhaSecurity.getContext().getCache().set(token, accessToken, accessTokenExpiresIn * 1000);
        IhaSecurity.getContext().getCache().set(refreshToken, accessToken, refreshTokenExpiresIn * 1000);
        return accessToken;
    }

    public static AccessToken refreshAccessToken(UserDetails userDetails, ClientDetails clientDetails, AccessToken accessToken, String nonce, String issuer) {
        String rawToken = accessToken.getAccessToken();
        long accessTokenExpiresIn = OAuth2Util.getAccessTokenExpiresIn(clientDetails.getAccessTokenTimeToLive());
        String accessTokenStr = JwtUtil.createJwtToken(clientDetails.getClientId(), userDetails, accessTokenExpiresIn, nonce, issuer);
        accessToken.setAccessToken(accessTokenStr);
        accessToken.setAccessTokenExpiresIn(accessTokenExpiresIn);

        accessToken.setAccessTokenExpiration(OAuth2Util.getAccessTokenExpiresAt(clientDetails.getAccessTokenTimeToLive()));

        String tokenCacheKey = IhaServerConstants.OAUTH_ACCESS_TOKEN_CACHE_KEY + accessTokenStr;
        IhaSecurity.getContext().getCache().set(tokenCacheKey, accessToken, accessTokenExpiresIn * 1000);

        String rawTokenCacheKey = IhaServerConstants.OAUTH_ACCESS_TOKEN_CACHE_KEY + rawToken;
        IhaSecurity.getContext().getCache().removeKey(rawTokenCacheKey);
        return accessToken;
    }

    public static AccessToken createClientCredentialsAccessToken(ClientDetails clientDetails, String grantType, String scope, String nonce, String issuer) {
        return createAccessToken(null, clientDetails, grantType, scope, nonce, issuer);
    }


    public static void invalidateToken(HttpServletRequest request) {
        String accessTokenStr = TokenUtil.getAccessToken(request);
        AccessToken accessToken = TokenUtil.getByAccessToken(accessTokenStr);
        if (null != accessToken) {
            String token = IhaServerConstants.OAUTH_ACCESS_TOKEN_CACHE_KEY + accessTokenStr;
            String rtoken = IhaServerConstants.OAUTH_REFRESH_TOKEN_CACHE_KEY + accessToken.getRefreshToken();
            IhaSecurity.getContext().getCache().removeKey(token);
            IhaSecurity.getContext().getCache().removeKey(rtoken);
        }
    }

    public static void validateAccessToken(String accessToken) {

        AccessToken token = getByAccessToken(accessToken);

        if (token == null) {
            throw new InvalidTokenException(ErrorResponse.INVALID_TOKEN);
        }

        LocalDateTime nowDateTime = DateUtil.nowDate();

        if (token.getAccessTokenExpiration().isBefore(nowDateTime)) {
            throw new InvalidTokenException(ErrorResponse.EXPIRED_TOKEN);
        }

    }

    public static void validateRefreshToken(String refreshToken) {

        AccessToken token = getByRefreshToken(refreshToken);

        if (token == null) {
            throw new InvalidTokenException(ErrorResponse.INVALID_TOKEN);
        }

        LocalDateTime nowDateTime = DateUtil.nowDate();

        if (token.getRefreshTokenExpiration().isBefore(nowDateTime)) {
            throw new InvalidTokenException(ErrorResponse.EXPIRED_TOKEN);
        }
    }

    public static AccessToken getByAccessToken(String accessToken) {
        if (null == accessToken) {
            return null;
        }
        accessToken = BearerToken.parse(accessToken);
        String token = IhaServerConstants.OAUTH_ACCESS_TOKEN_CACHE_KEY + accessToken;
        return (AccessToken) IhaSecurity.getContext().getCache().get(token);
    }

    public static AccessToken getByRefreshToken(String refreshToken) {
        if (null == refreshToken) {
            return null;
        }
        String token = IhaServerConstants.OAUTH_REFRESH_TOKEN_CACHE_KEY + refreshToken;
        return (AccessToken) IhaSecurity.getContext().getCache().get(token);
    }
}
