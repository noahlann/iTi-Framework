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
import lombok.val;
import org.lan.iti.common.core.util.StringUtil;
import org.lan.iti.common.core.util.idgen.IdGenerator;
import org.lan.iti.iha.oauth2.OAuth2ParameterNames;
import org.lan.iti.iha.oauth2.exception.ExpiredTokenException;
import org.lan.iti.iha.oauth2.exception.InvalidTokenException;
import org.lan.iti.iha.oauth2.token.BearerToken;
import org.lan.iti.iha.security.IhaSecurity;
import org.lan.iti.iha.security.cache.Cache;
import org.lan.iti.iha.security.clientdetails.ClientDetails;
import org.lan.iti.iha.security.userdetails.UserDetails;
import org.lan.iti.iha.security.util.RequestUtil;
import org.lan.iti.iha.server.IhaServerConstants;
import org.lan.iti.iha.server.helper.JwtHelper;
import org.lan.iti.iha.server.model.AuthorizationToken;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@UtilityClass
public class AuthorizationTokenUtil {

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
        return createIdToken(clientDetails, userDetails, nonce, null, issuer);
    }

    public static String createIdToken(ClientDetails clientDetails, UserDetails userDetails, String nonce, String scopes, String issuer) {
        long idTokenExpiresIn = OAuth2Util.getIdTokenExpiresIn(clientDetails.getIdTokenTimeToLive());
        return JwtHelper.createJwtToken(issuer,
                clientDetails.getClientId(),
                userDetails,
                nonce,
                idTokenExpiresIn,
                scopes);
    }

    public static AuthorizationToken createAccessToken(UserDetails userDetails,
                                                       ClientDetails clientDetails,
                                                       String grantType,
                                                       String scope,
                                                       String nonce,
                                                       String issuer,
                                                       boolean cache) {
        String clientId = clientDetails.getClientId();

        long accessTokenExpiresIn = OAuth2Util.getAccessTokenExpiresIn(clientDetails.getAccessTokenTimeToLive());
        long refreshTokenExpiresIn = OAuth2Util.getRefreshTokenExpiresIn(clientDetails.getRefreshTokenTimeToLive());

        String accessTokenStr = JwtHelper.createJwtToken(issuer, clientId, userDetails, nonce, accessTokenExpiresIn);
        String refreshTokenStr = SecureUtil.sha256(clientId.concat(scope).concat(System.currentTimeMillis() + ""));

        val builder = AuthorizationToken.builder()
                .id(IdGenerator.nextStr())
                .accessToken(accessTokenStr)
                .refreshToken(refreshTokenStr)
                .grantType(grantType)
                .tokenType(OAuth2ParameterNames.TOKEN_TYPE_BEARER)
                .clientId(clientId)
                .scope(scope)
                .accessTokenExpiresIn(accessTokenExpiresIn)
                .accessTokenExpiration(OAuth2Util.getAccessTokenExpiresAt(accessTokenExpiresIn))
                .refreshTokenExpiresIn(refreshTokenExpiresIn)
                .refreshTokenExpiration(OAuth2Util.getRefreshTokenExpiresAt(refreshTokenExpiresIn));
        if (null != userDetails) {
            builder.userId(userDetails.getId())
                    .unionId(userDetails.getUnionId());
        }
        if (OAuth2Util.isOidcProtocol(scope)) {
            long idTokenExpiresIn = OAuth2Util.getIdTokenExpiresIn(clientDetails.getIdTokenTimeToLive());
            LocalDateTime idTokenExpiration = OAuth2Util.getIdTokenExpiresAt(idTokenExpiresIn);
            String idTokenStr = createIdToken(clientDetails, userDetails, nonce, scope, issuer);

            builder.idToken(idTokenStr)
                    .idTokenExpiresIn(idTokenExpiresIn)
                    .idTokenExpiration(idTokenExpiration);
        }
        AuthorizationToken token = builder.build();
        if (cache) {
            cacheAuthorizationToken(token, null);
        }
        return token;
    }

    public static void evictAuthorizationToken(AuthorizationToken token,
                                               boolean setNull,
                                               boolean accessToken,
                                               boolean refreshToken,
                                               boolean idToken) {
        if (token == null) {
            return;
        }
        Cache cache = IhaSecurity.getContext().getCache();

        if (accessToken && StringUtil.isNotEmpty(token.getAccessToken())) {
            String rak = IhaServerConstants.OAUTH_ACCESS_TOKEN_CACHE_KEY + token.getAccessToken();
            cache.evict(rak);

            if (setNull) {
                token.setAccessToken(null)
                        .setAccessTokenExpiration(null)
                        .setAccessTokenExpiresIn(null);
            }
        }

        if (refreshToken && StringUtil.isNotEmpty(token.getRefreshToken())) {
            String rak = IhaServerConstants.OAUTH_REFRESH_TOKEN_CACHE_KEY + token.getRefreshToken();
            cache.evict(rak);

            if (setNull) {
                token.setRefreshToken(null)
                        .setRefreshTokenExpiration(null)
                        .setRefreshTokenExpiresIn(null);
            }
        }

        if (idToken && StringUtil.isNotEmpty(token.getIdToken())) {
            String rak = IhaServerConstants.OAUTH_ID_TOKEN_CACHE_KEY + token.getIdToken();
            cache.evict(rak);

            if (setNull) {
                token.setIdToken(null)
                        .setIdTokenExpiration(null)
                        .setIdTokenExpiresIn(null);
            }
        }
    }

    public static void cacheAuthorizationToken(AuthorizationToken token, AuthorizationToken rawToken) {
        if (token == null) {
            return;
        }
        // 清理rawToken的缓存
        evictAuthorizationToken(rawToken, false,
                true, true, true);

        Cache cache = IhaSecurity.getContext().getCache();
        if (StringUtil.isNotEmpty(token.getAccessToken())) {
            String ak = IhaServerConstants.OAUTH_ACCESS_TOKEN_CACHE_KEY + token.getAccessToken();
            cache.put(ak, token, token.getAccessTokenExpiresIn(), TimeUnit.SECONDS);
        }
        if (StringUtil.isNotEmpty(token.getRefreshToken())) {
            String rk = IhaServerConstants.OAUTH_REFRESH_TOKEN_CACHE_KEY + token.getRefreshToken();
            cache.put(rk, token, token.getRefreshTokenExpiresIn(), TimeUnit.SECONDS);
        }
        if (StringUtil.isNotEmpty(token.getIdToken())) {
            String ik = IhaServerConstants.OAUTH_ID_TOKEN_CACHE_KEY + token.getIdToken();
            cache.put(ik, token, token.getIdTokenExpiresIn(), TimeUnit.SECONDS);
        }
    }

    /**
     * 刷新令牌
     *
     * @param userDetails   用户信息
     * @param clientDetails 客户端信息
     * @param rawToken      原始Token
     * @param nonce         nonce
     * @param issuer        issuer
     * @return 新的令牌
     * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#RefreshTokens">12.1.  Refresh Request</a>
     */
    public static AuthorizationToken refreshAccessToken(UserDetails userDetails, ClientDetails clientDetails, AuthorizationToken rawToken, String nonce, String issuer) {
        String clientId = clientDetails.getClientId();

        // copy
        AuthorizationToken newToken = rawToken.toBuilder().build();

        long accessTokenExpiresIn = OAuth2Util.getAccessTokenExpiresIn(clientDetails.getAccessTokenTimeToLive());
        String accessTokenStr = JwtHelper.createJwtToken(issuer, clientDetails.getClientId(), userDetails, nonce, accessTokenExpiresIn);
        newToken.setAccessToken(accessTokenStr)
                .setAccessTokenExpiresIn(accessTokenExpiresIn)
                .setAccessTokenExpiration(OAuth2Util.getAccessTokenExpiresAt(clientDetails.getAccessTokenTimeToLive()));

        if (!clientDetails.isReuseRefreshToken()) {
            long refreshTokenExpiresIn = OAuth2Util.getRefreshTokenExpiresIn(clientDetails.getRefreshTokenTimeToLive());
            String refreshTokenStr = SecureUtil.sha256(clientId.concat(rawToken.getScope()).concat(System.currentTimeMillis() + ""));

            newToken.setRefreshToken(refreshTokenStr)
                    .setRefreshTokenExpiresIn(refreshTokenExpiresIn)
                    .setRefreshTokenExpiration(OAuth2Util.getRefreshTokenExpiresAt(clientDetails.getRefreshTokenTimeToLive()));
        }

        /*
         * TODO refresh id_token
         *  iss/sub/aud/auth_time 需与之前相同 (auth_time可不存在)
         *  iat 必须是新的时间
         *  azp 如果原token中存在，则必须与之前使用相同的生成规则
         */
        cacheAuthorizationToken(newToken, rawToken);
        return newToken;
    }

    public static AuthorizationToken createClientCredentialsAccessToken(ClientDetails clientDetails, String grantType, String scope, String nonce, String issuer) {
        return createAccessToken(null, clientDetails, grantType, scope, nonce, issuer, false);
    }

    public static void invalidateToken(HttpServletRequest request) {
        String accessTokenStr = AuthorizationTokenUtil.getAccessToken(request);
        AuthorizationToken authorizationToken = AuthorizationTokenUtil.getByAccessToken(accessTokenStr);
        if (null != authorizationToken) {
            String token = IhaServerConstants.OAUTH_ACCESS_TOKEN_CACHE_KEY + accessTokenStr;
            String rtoken = IhaServerConstants.OAUTH_REFRESH_TOKEN_CACHE_KEY + authorizationToken.getRefreshToken();
            IhaSecurity.getContext().getCache().evict(token, rtoken);
        }
    }

    public static void validateAccessToken(String accessToken) {
        AuthorizationToken token = getByAccessToken(accessToken);

        if (token == null) {
            throw new InvalidTokenException();
        }

        LocalDateTime nowDateTime = DateUtil.nowDate();
        if (token.getAccessTokenExpiration().isBefore(nowDateTime)) {
            throw new ExpiredTokenException();
        }
    }

    public static void validateRefreshToken(String refreshToken) {
        AuthorizationToken token = getByRefreshToken(refreshToken);

        if (token == null) {
            throw new InvalidTokenException();
        }

        LocalDateTime nowDateTime = DateUtil.nowDate();

        if (token.getRefreshTokenExpiration().isBefore(nowDateTime)) {
            throw new ExpiredTokenException();
        }
    }

    public static AuthorizationToken getByAccessToken(String accessToken) {
        if (null == accessToken) {
            return null;
        }
        accessToken = BearerToken.parse(accessToken);
        String token = IhaServerConstants.OAUTH_ACCESS_TOKEN_CACHE_KEY + accessToken;
        return (AuthorizationToken) IhaSecurity.getContext().getCache().get(token);
    }

    public static AuthorizationToken getByRefreshToken(String refreshToken) {
        if (null == refreshToken) {
            return null;
        }
        String token = IhaServerConstants.OAUTH_REFRESH_TOKEN_CACHE_KEY + refreshToken;
        return (AuthorizationToken) IhaSecurity.getContext().getCache().get(token);
    }
}
