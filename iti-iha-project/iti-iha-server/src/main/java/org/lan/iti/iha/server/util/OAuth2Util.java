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

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.*;
import cn.hutool.crypto.SecureUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jose4j.base64url.Base64Url;
import org.lan.iti.common.core.util.StringUtil;
import org.lan.iti.iha.oauth2.GrantType;
import org.lan.iti.iha.oauth2.OAuth2Constants;
import org.lan.iti.iha.oauth2.OAuth2ParameterNames;
import org.lan.iti.iha.oauth2.exception.*;
import org.lan.iti.iha.oauth2.pkce.CodeChallengeMethod;
import org.lan.iti.iha.oauth2.pkce.PkceParams;
import org.lan.iti.iha.oidc.OidcParameterNames;
import org.lan.iti.iha.security.IhaSecurity;
import org.lan.iti.iha.security.clientdetails.ClientDetails;
import org.lan.iti.iha.security.clientdetails.ClientDetailsService;
import org.lan.iti.iha.security.userdetails.UserDetails;
import org.lan.iti.iha.server.IhaServerConstants;
import org.lan.iti.iha.server.model.AuthorizationCode;
import org.lan.iti.iha.server.security.IhaServerRequestParam;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@UtilityClass
@Slf4j
public class OAuth2Util {
    private static final Collection<String> REDIRECT_GRANT_TYPES = Arrays.asList("implicit", "authorization_code");

    /**
     * @param requestScopes The scope parameter in the current HTTP request
     * @param clientScopes  Scope in client detail
     * @return After the verification is passed, return the scope list
     */
    public static List<String> validateScope(String requestScopes, String clientScopes) {
        if (StrUtil.isEmpty(requestScopes)) {
            throw new InvalidScopeException();
        }
        List<String> scopes = StringUtil.split(requestScopes, OAuth2Constants.SCOPE_SEPARATOR);

        if (StrUtil.isNotEmpty(clientScopes)) {
            List<String> appScopes = StringUtil.split(clientScopes, OAuth2Constants.SCOPE_SEPARATOR);
            for (String scope : scopes) {
                if (!appScopes.contains(scope)) {
                    throw new InvalidScopeException("Invalid scope: " + scope + ". Only the following scopes are supported: " + clientScopes);
                }
            }
        }

        return scopes;
    }


    /**
     * Determine whether the current grant type supports redirect uri
     *
     * @param grantTypes some grant types
     * @return true if the supplied grant types includes one or more of the redirect types
     */
    private static boolean containsRedirectGrantType(List<String> grantTypes) {
        for (String type : grantTypes) {
            if (REDIRECT_GRANT_TYPES.contains(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verify the callback url
     *
     * @param requestRedirectUri The callback url passed in the current HTTP request
     * @param clientDetails      client detail
     */
    public static void validateRedirectUri(String requestRedirectUri, ClientDetails clientDetails) {
        String clientGrantTypes = clientDetails.getGrantTypes();
        List<String> clientGrantTypeSet = StringUtil.split(clientGrantTypes, OAuth2Constants.SCOPE_SEPARATOR);
        if (clientGrantTypeSet.isEmpty()) {
            throw new InvalidGrantException("A client must have at least one authorized grant type.");
        }
        if (!containsRedirectGrantType(clientGrantTypeSet)) {
            throw new InvalidGrantException(
                    "A redirect_uri can only be used by implicit or authorization_code grant types.");
        }

        List<String> clientRedirectUris = StrUtil.split(clientDetails.getRedirectUris(), OAuth2Constants.SCOPE_SEPARATOR);
        if (requestRedirectUri == null || !clientRedirectUris.contains(requestRedirectUri)) {
            throw new InvalidRedirectUriException();
        }
    }

    /**
     * 1. Only the authorization code mode will verify pkce, and the client secret will be verified when pkce is not enabled
     * <p>
     * 2. Implicit authorization grant and password authorization grant do not need to verify client secret
     * <p>
     * 3. Client authorizatio grantn needs to verify the client secret
     *
     * @param param         request params
     * @param clientDetails client detail
     */
    public static void validateSecret(IhaServerRequestParam param, ClientDetails clientDetails) {
        if (param.getGrantType().equals(GrantType.AUTHORIZATION_CODE.getType())) {
            if (param.isEnablePkce()) {
                OAuth2Util.validateAuthorizationCodeChallenge(param.getCodeVerifier(), param.getCode());
                return;
            }
        }
        matchesSecret(clientDetails.getClientSecret(), param.getClientSecret());
    }

    private static void matchesSecret(String rawSecret, String encodedSecret) {
        ClientDetailsService clientDetailsService = IhaSecurity.getContext().getClientDetailsService();
        if (clientDetailsService == null) {
            throw new SecurityException(String.format("%s has not been injected", ClientDetailsService.class.getName()));
        }
        if (!clientDetailsService.matches(rawSecret, encodedSecret)) {
            throw new InvalidClientException();
        }
    }

    /**
     * When the pkce protocol is enabled, the code challenge needs to be verified
     *
     * @param codeVerifier code verifier
     * @param code         authorization code
     * @see <a href="https://tools.ietf.org/html/rfc7636">https://tools.ietf.org/html/rfc7636</a>
     */
    public static void validateAuthorizationCodeChallenge(String codeVerifier, String code) {
        log.debug("The client opened the pkce enhanced protocol and began to verify the legitimacy of the code challenge...");
        AuthorizationCode authCode = getCodeInfo(code);
        if (ObjectUtil.isNull(authCode)) {
            throw new InvalidCodeException();
        }
        if (ObjectUtil.hasNull(authCode.getCodeChallenge(), authCode.getCodeChallengeMethod())) {
            log.debug("The client opened the pkce enhanced protocol, and the legality verification of the code challenge failed...");
            throw new InvalidCodeChallengeException();
        }
        String codeChallengeMethod = authCode.getCodeChallengeMethod();
        String cacheCodeChallenge = authCode.getCodeChallenge();
        String currentCodeChallenge = OAuth2Util.generateCodeChallenge(codeChallengeMethod, codeVerifier);
        if (!currentCodeChallenge.equals(cacheCodeChallenge)) {
            throw new InvalidCodeChallengeException();
        }
    }

    /**
     * Obtain auth code info by authorization code
     *
     * @param code authorization code
     * @return string
     */
    public static AuthorizationCode getCodeInfo(String code) {
        return (AuthorizationCode) IhaSecurity.getContext().getCache().get(IhaServerConstants.OAUTH_CODE_CACHE_KEY + code);
    }

    /**
     * Verify the response type
     *
     * @param requestResponseType The response type in the current HTTP request
     * @param clientResponseTypes Response type in client detail
     */
    public static void validateResponseType(String requestResponseType, String clientResponseTypes) {
        List<String> clientResponseTypeSet = StringUtil.split(clientResponseTypes, OAuth2Constants.SCOPE_SEPARATOR);
        if (!StrUtil.isEmpty(clientResponseTypes) && !clientResponseTypeSet.contains(requestResponseType)) {
            throw new UnsupportedResponseTypeException();
        }
    }

    /**
     * Verify the grant type
     *
     * @param requestGrantType The grant type in the current HTTP request
     * @param clientGrantTypes Grant type in client detail
     * @param equalTo          {@code requestGrantType} Must match grant type value
     */
    public static void validateGrantType(String requestGrantType, String clientGrantTypes, GrantType equalTo) {
        List<String> grantTypeSet = StringUtil.split(clientGrantTypes, OAuth2Constants.SCOPE_SEPARATOR);
        if (StrUtil.isEmpty(requestGrantType) || ArrayUtil.isEmpty(grantTypeSet) || !grantTypeSet.contains(requestGrantType)) {
            throw new UnsupportedGrantTypeException();
        }
        if (null != equalTo && !requestGrantType.equals(equalTo.getType())) {
            throw new UnsupportedGrantTypeException();
        }
    }

    public static void validClientDetails(ClientDetails clientDetails) {
        if (clientDetails == null) {
            throw new InvalidClientException();
        }
        if (!clientDetails.isAvailable()) {
            throw new DisabledClientException();
        }
    }

    /**
     * Verification authorization code
     *
     * @param grantType grant Type
     * @param code      authorization code
     * @return AuthCode
     */
    public AuthorizationCode validateAndGetAuthorizationCode(String grantType, String code) {
        if (!GrantType.AUTHORIZATION_CODE.getType().equals(grantType)) {
            throw new UnsupportedGrantTypeException();
        }
        AuthorizationCode authCode = getCodeInfo(code);
        if (null == authCode || ObjectUtil.hasNull(authCode.getUserDetails(), authCode.getScope())) {
            throw new InvalidCodeException();
        }
        return authCode;
    }

    /**
     * Delete authorization code
     *
     * @param code authorization code
     */
    public static void invalidateCode(String code) {
        IhaSecurity.getContext().getCache().evict(IhaServerConstants.OAUTH_CODE_CACHE_KEY + code);
    }

    /**
     * Generate authorization code
     *
     * @param param         Parameters requested by the client
     * @param userDetails   User Info
     * @param codeExpiresIn code expiration time
     * @return String
     */
    public static String createAuthorizationCode(IhaServerRequestParam param, UserDetails userDetails, Long codeExpiresIn) {
        String code = RandomUtil.randomString(12);
        AuthorizationCode authorizationCode = AuthorizationCode.builder()
                .userDetails(userDetails)
                .scope(param.getScope())
                .nonce(param.getNonce())
                .codeChallenge(param.getCodeChallenge())
                .codeChallengeMethod(param.getCodeChallengeMethod())
                .build();
        IhaSecurity.getContext().getCache().put(IhaServerConstants.OAUTH_CODE_CACHE_KEY + code,
                authorizationCode, codeExpiresIn, TimeUnit.SECONDS);
        return code;
    }

    /**
     * 获取过期时长 秒为单位
     *
     * @param ttl        过期时长
     * @param defaultTtl 默认时长
     * @return 时长
     */
    public static long getExpiresIn(Long ttl, Long defaultTtl) {
        if (ttl == null || ttl <= 0) {
            return defaultTtl;
        }
        return ttl;
    }

    /**
     * Get the expiration time of access token, default is {@link IhaServerConstants#DEFAULT_ACCESS_TOKEN_TIME_TO_LIVE}
     *
     * @param ttl The expiration time of the access token in the client detail
     * @return long
     */
    public static long getAccessTokenExpiresIn(Long ttl) {
        return getExpiresIn(ttl, IhaServerConstants.DEFAULT_ACCESS_TOKEN_TIME_TO_LIVE);
    }

    /**
     * Get the expiration time of refresh token, the default is {@link IhaServerConstants#DEFAULT_REFRESH_TOKEN_TIME_TO_LIVE}
     *
     * @param ttl The expiration time of the refresh token in the client detail
     * @return long
     */
    public static long getRefreshTokenExpiresIn(Long ttl) {
        return getExpiresIn(ttl, IhaServerConstants.DEFAULT_REFRESH_TOKEN_TIME_TO_LIVE);
    }

    /**
     * Get the expiration time of the authorization code code, the default is {@link IhaServerConstants#DEFAULT_AUTHORIZATION_CODE_TIME_TO_LIVE}
     *
     * @param ttl The expiration time of the code in the client detail
     * @return long
     */
    public static long getCodeExpiresIn(Long ttl) {
        return getExpiresIn(ttl, IhaServerConstants.DEFAULT_AUTHORIZATION_CODE_TIME_TO_LIVE);
    }

    /**
     * Get the expiration time of id token, the default is{@link IhaServerConstants#DEFAULT_ID_TOKEN_TIME_TO_LIVE}
     *
     * @param ttl The expiration time of the id token in the client detail
     * @return long
     */
    public static long getIdTokenExpiresIn(Long ttl) {
        return getExpiresIn(ttl, IhaServerConstants.DEFAULT_ID_TOKEN_TIME_TO_LIVE);
    }

    /**
     * 获取具体的过期时间点
     *
     * @param ttl 过期时长
     * @return 过期时间点
     */
    public static LocalDateTime getExpiresAt(long ttl) {
        return DateUtil.ofEpochSecond(System.currentTimeMillis() + ttl * 1000, null);
    }

    /**
     * Get the expiration deadline of access token
     *
     * @param ttl The expiration time of the access token in the client detail
     * @return long
     */
    public static LocalDateTime getAccessTokenExpiresAt(Long ttl) {
        return getExpiresAt(getAccessTokenExpiresIn(ttl));
    }

    /**
     * Get the expiration deadline of refresh token
     *
     * @param ttl The expiration time of the refresh token in the client detail
     * @return long
     */
    public static LocalDateTime getRefreshTokenExpiresAt(Long ttl) {
        return getExpiresAt(getRefreshTokenExpiresIn(ttl));
    }

    /**
     * Get the expiration deadline of authorization code
     *
     * @param ttl The expiration time of the code in the client detail
     * @return long
     */
    public static LocalDateTime getCodeExpiresAt(Long ttl) {
        return getExpiresAt(getCodeExpiresIn(ttl));
    }

    /**
     * Get the expiration deadline of id token
     *
     * @param ttl The expiration time of the id token in the client detail
     * @return long
     */
    public static LocalDateTime getIdTokenExpiresAt(Long ttl) {
        return getExpiresAt(getIdTokenExpiresIn(ttl));
    }

    /**
     * Create authorize url
     *
     * @param authorizeUrl authorize url
     * @param param        request params
     * @return authorizeUrl
     */
    public static String createAuthorizeUrl(String authorizeUrl, IhaServerRequestParam param) {
        Map<String, Object> model = new HashMap<>(13);
        model.put(OAuth2ParameterNames.CLIENT_ID, param.getClientId());

        if (StrUtil.isNotEmpty(param.getRedirectUri())) {
            model.put(OAuth2ParameterNames.REDIRECT_URI, param.getRedirectUri());
        }

        if (StrUtil.isNotEmpty(param.getScope())) {
            model.put(OAuth2ParameterNames.SCOPE, param.getScope());
        }

        if (StrUtil.isNotEmpty(param.getState())) {
            model.put(OAuth2ParameterNames.STATE, param.getState());
        }

        if (StrUtil.isNotEmpty(param.getUid())) {
            model.put(IhaServerConstants.UID, param.getUid());
        }

        if (StrUtil.isNotEmpty(param.getNonce())) {
            model.put(OidcParameterNames.NONCE, param.getNonce());
        }

        if (StrUtil.isNotEmpty(param.getResponseType())) {
            model.put(OAuth2ParameterNames.RESPONSE_TYPE, param.getResponseType());
        }
        if (StrUtil.isNotEmpty(param.getCodeChallengeMethod()) || StrUtil.isNotEmpty(param.getCodeChallenge())) {
            model.put(PkceParams.CODE_CHALLENGE_METHOD, param.getCodeChallengeMethod());
            model.put(PkceParams.CODE_CHALLENGE, param.getCodeChallenge());
        }
        if (StringUtil.isNotEmpty(param.getAutoApprove())) {
            model.put(OAuth2ParameterNames.AUTOAPPROVE, param.getAutoApprove());
        }
        String uriParams = URLUtil.buildQuery(model, StandardCharsets.UTF_8);
        if (authorizeUrl.contains("?")) {
            authorizeUrl = authorizeUrl + (authorizeUrl.endsWith("?") ? "" : "&") + uriParams;
        } else {
            authorizeUrl = authorizeUrl + "?" + uriParams;
        }
        return authorizeUrl;
    }

    public static boolean isOidcProtocol(String scopes) {
        return StringUtil.contains(scopes, "openid");
    }

    /**
     * Suitable for oauth 2.0 pkce enhanced protocol
     *
     * @param codeChallengeMethod s256 / plain
     * @param codeVerifier        code verifier, Generated by the developer
     * @return code challenge
     */
    public static String generateCodeChallenge(String codeChallengeMethod, String codeVerifier) {
        if (CodeChallengeMethod.S256.name().equalsIgnoreCase(codeChallengeMethod)) {
            // https://tools.ietf.org/html/rfc7636#section-4.2
            // code_challenge = BASE64URL-ENCODE(SHA256(ASCII(code_verifier)))
            return Base64.encodeUrlSafe(SecureUtil.sha256().digest(codeVerifier));
        } else {
            return codeVerifier;
        }
    }

    /**
     * Suitable for oauth 2.0 pkce enhanced protocol
     *
     * @return code verifier
     */
    public static String generateCodeVerifier() {
        return Base64Url.encode(RandomUtil.randomString(50), "UTF-8");
    }
}
