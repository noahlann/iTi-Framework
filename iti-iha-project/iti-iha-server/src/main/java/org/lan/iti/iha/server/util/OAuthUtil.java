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
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.SecureUtil;
import lombok.experimental.UtilityClass;
import org.jose4j.base64url.Base64Url;
import org.lan.iti.iha.oauth2.GrantType;
import org.lan.iti.iha.oauth2.OAuth2ParameterNames;
import org.lan.iti.iha.oauth2.pkce.PkceCodeChallengeMethod;
import org.lan.iti.iha.oauth2.pkce.PkceParams;
import org.lan.iti.iha.oidc.OidcParameterNames;
import org.lan.iti.iha.server.IhaServerConstants;
import org.lan.iti.iha.server.exception.*;
import org.lan.iti.iha.server.model.ClientDetails;
import org.lan.iti.iha.server.model.IhaServerRequestParam;
import org.lan.iti.iha.server.model.enums.ErrorResponse;
import org.lan.iti.iha.server.service.OAuth2Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@UtilityClass
public class OAuthUtil {
    private static final Collection<String> REDIRECT_GRANT_TYPES = Arrays.asList("implicit", "authorization_code");

    /**
     * @param requestScopes The scope parameter in the current HTTP request
     * @param clientScopes  Scope in client detail
     * @return After the verification is passed, return the scope list
     */
    public static Set<String> validateScope(String requestScopes, String clientScopes) {

        if (StrUtil.isEmpty(requestScopes)) {
            throw new InvalidScopeException(ErrorResponse.INVALID_SCOPE);
        }
        Set<String> scopes = StringUtil.convertStrToList(requestScopes);

        if (StrUtil.isNotEmpty(clientScopes)) {
            Set<String> appScopes = StringUtil.convertStrToList(clientScopes);
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
    private static boolean containsRedirectGrantType(Set<String> grantTypes) {
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
     * @param clientDetail       client detail
     */
    public static void validateRedirectUri(String requestRedirectUri, ClientDetails clientDetail) {
        String clientGrantTypes = clientDetail.getGrantTypes();
        Set<String> clientGrantTypeSet = StringUtil.convertStrToList(clientGrantTypes);
        if (clientGrantTypeSet.isEmpty()) {
            throw new InvalidGrantException("A client must have at least one authorized grant type.");
        }
        if (!containsRedirectGrantType(clientGrantTypeSet)) {
            throw new InvalidGrantException(
                    "A redirect_uri can only be used by implicit or authorization_code grant types.");
        }

        List<String> clientRedirectUris = StrUtil.split(clientDetail.getRedirectUris(), IhaServerConstants.SPACE);
        if (requestRedirectUri == null || !clientRedirectUris.contains(requestRedirectUri)) {
            throw new InvalidRedirectUriException(ErrorResponse.INVALID_REDIRECT_URI);
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
     * @param oauth2Service oauth2Service
     */
    public static void validateSecret(IhaServerRequestParam param, ClientDetails clientDetails, OAuth2Service oauth2Service) {
        if (param.getGrantType().equals(GrantType.AUTHORIZATION_CODE.getType())) {
            if (param.isEnablePkce()) {
                oauth2Service.validateAuthorizationCodeChallenge(param.getCodeVerifier(), param.getCode());
            } else {
                if (StrUtil.isEmpty(param.getClientSecret()) || !clientDetails.getClientSecret().equals(param.getClientSecret())) {
                    throw new InvalidClientException(ErrorResponse.INVALID_CLIENT);
                }
            }
        } else {
            if (StrUtil.isEmpty(param.getClientSecret()) || !clientDetails.getClientSecret().equals(param.getClientSecret())) {
                throw new InvalidClientException(ErrorResponse.INVALID_CLIENT);
            }
        }
    }

    /**
     * Verify the response type
     *
     * @param requestResponseType The response type in the current HTTP request
     * @param clientResponseTypes Response type in client detail
     */
    public static void validateResponseType(String requestResponseType, String clientResponseTypes) {
        Set<String> clientResponseTypeSet = StringUtil.convertStrToList(clientResponseTypes);
        if (!StrUtil.isEmpty(clientResponseTypes) && !clientResponseTypeSet.contains(requestResponseType)) {
            throw new UnsupportedResponseTypeException(ErrorResponse.UNSUPPORTED_RESPONSE_TYPE);
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
        Set<String> grantTypeSet = StringUtil.convertStrToList(clientGrantTypes);
        if (StrUtil.isEmpty(requestGrantType) || ArrayUtil.isEmpty(grantTypeSet) || !grantTypeSet.contains(requestGrantType)) {
            throw new UnsupportedGrantTypeException(ErrorResponse.UNSUPPORTED_GRANT_TYPE);
        }
        if (null != equalTo && !requestGrantType.equals(equalTo.getType())) {
            throw new UnsupportedGrantTypeException(ErrorResponse.UNSUPPORTED_GRANT_TYPE);
        }
    }

    public static void validClientDetail(ClientDetails clientDetails) {
        if (clientDetails == null) {
            throw new InvalidClientException(ErrorResponse.INVALID_CLIENT);
        }
        if (!Optional.ofNullable(clientDetails.getAvailable()).orElse(false)) {
            throw new InvalidClientException(ErrorResponse.DISABLED_CLIENT);
        }
    }

    /**
     * Get the expiration time of access token, default is {@link IhaServerConstants#ACCESS_TOKEN_ACTIVITY_TIME}
     *
     * @param ttl The expiration time of the access token in the client detail
     * @return long
     */
    public static long getAccessTokenExpiresIn(Long ttl) {
        return Optional.ofNullable(ttl).orElse(IhaServerConstants.ACCESS_TOKEN_ACTIVITY_TIME);
    }

    /**
     * Get the expiration time of refresh token, the default is {@link IhaServerConstants#REFRESH_TOKEN_ACTIVITY_TIME}
     *
     * @param ttl The expiration time of the refresh token in the client detail
     * @return long
     */
    public static long getRefreshTokenExpiresIn(Long ttl) {
        return Optional.ofNullable(ttl).orElse(IhaServerConstants.REFRESH_TOKEN_ACTIVITY_TIME);
    }

    /**
     * Get the expiration time of the authorization code code, the default is {@link IhaServerConstants#AUTHORIZATION_CODE_ACTIVITY_TIME}
     *
     * @param ttl The expiration time of the code in the client detail
     * @return long
     */
    public static long getCodeExpiresIn(Long ttl) {
        return Optional.ofNullable(ttl).orElse(IhaServerConstants.AUTHORIZATION_CODE_ACTIVITY_TIME);
    }

    /**
     * Get the expiration time of id token, the default is{@link IhaServerConstants#ID_TOKEN_ACTIVITY_TIME}
     *
     * @param ttl The expiration time of the id token in the client detail
     * @return long
     */
    public static long getIdTokenExpiresIn(Long ttl) {
        return Optional.ofNullable(ttl).orElse(IhaServerConstants.ID_TOKEN_ACTIVITY_TIME);
    }

    /**
     * Get the expiration deadline of access token
     *
     * @param ttl The expiration time of the access token in the client detail
     * @return long
     */
    public static LocalDateTime getAccessTokenExpiresAt(Long ttl) {
        long expiresIn = getAccessTokenExpiresIn(ttl);
        return DateUtil.ofEpochSecond(System.currentTimeMillis() + expiresIn * 1000, null);
    }

    /**
     * Get the expiration deadline of refresh token
     *
     * @param ttl The expiration time of the refresh token in the client detail
     * @return long
     */
    public static LocalDateTime getRefreshTokenExpiresAt(Long ttl) {
        long expiresIn = getRefreshTokenExpiresIn(ttl);
        return DateUtil.ofEpochSecond(System.currentTimeMillis() + expiresIn * 1000, null);
    }

    /**
     * Get the expiration deadline of authorization code
     *
     * @param ttl The expiration time of the code in the client detail
     * @return long
     */
    public static LocalDateTime getCodeExpiresAt(Long ttl) {
        long expiresIn = getCodeExpiresIn(ttl);
        return DateUtil.ofEpochSecond(System.currentTimeMillis() + expiresIn * 1000, null);
    }

    /**
     * Get the expiration deadline of id token
     *
     * @param ttl The expiration time of the id token in the client detail
     * @return long
     */
    public static LocalDateTime getIdTokenExpiresAt(Long ttl) {
        long expiresIn = getIdTokenExpiresIn(ttl);
        return DateUtil.ofEpochSecond(System.currentTimeMillis() + expiresIn * 1000, null);
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

    public static String generateClientId() {
        return RandomUtil.randomString(32);
    }

    public static String generateClientSecret() {
        return RandomUtil.randomString(40);
    }

    public static boolean isOidcProtocol(String scopes) {
        Set<String> scopeList = StringUtil.convertStrToList(scopes);
        return scopeList.contains("openid");
    }

    /**
     * Suitable for oauth 2.0 pkce enhanced protocol
     *
     * @param codeChallengeMethod s256 / plain
     * @param codeVerifier        code verifier, Generated by the developer
     * @return code challenge
     */
    public static String generateCodeChallenge(String codeChallengeMethod, String codeVerifier) {
        if (PkceCodeChallengeMethod.S256.name().equalsIgnoreCase(codeChallengeMethod)) {
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
