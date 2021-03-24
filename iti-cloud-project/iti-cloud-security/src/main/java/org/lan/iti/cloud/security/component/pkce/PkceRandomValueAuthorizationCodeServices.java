/*
 *
 *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.cloud.security.component.pkce;

import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.Map;

/**
 * 支持Pkce的随机code生成
 *
 * @author NorthLan
 * @date 2020-08-05
 * @url https://noahlan.com
 */
public abstract class PkceRandomValueAuthorizationCodeServices implements PkceAuthorizationCodeServices {
    private final RandomValueStringGenerator generator = new RandomValueStringGenerator(32);
    private final ClientDetailsService clientDetailsService;

    protected PkceRandomValueAuthorizationCodeServices(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }

    protected abstract void store(String code, PkceProtectedAuthentication authentication);

    protected abstract OAuth2Authentication remove(String code, @Nullable String codeVerifier);

    @Override
    public String createAuthorizationCode(OAuth2Authentication authentication) {
        String code = generator.generate();
        store(code, getProtectedAuthentication(authentication));
        return code;
    }

    @Override
    public OAuth2Authentication consumeAuthorizationCode(String code) throws InvalidGrantException {
        OAuth2Authentication auth = this.remove(code, null);
        if (auth == null) {
            throw new InvalidGrantException("Invalid authorization code: " + code);
        }
        return auth;
    }

    @Override
    public OAuth2Authentication consumeAuthorizationCodeAndCodeVerifier(String code, String codeVerifier) throws InvalidGrantException {
        OAuth2Authentication auth = this.remove(code, codeVerifier);
        if (auth == null) {
            throw new InvalidGrantException("Invalid authorization code: " + code);
        }
        return auth;
    }

    protected PkceProtectedAuthentication getProtectedAuthentication(OAuth2Authentication authentication) {
        Map<String, String> requestParameters = authentication.getOAuth2Request().getRequestParameters();
        if (isClientSupportPkce(requestParameters.get(OAuth2Utils.CLIENT_ID))) {
            if (!requestParameters.containsKey(KEY_CODE_CHALLENGE)) {
                throw new InvalidRequestException("Code challenge required.");
            }
            String codeChallenge = requestParameters.get(KEY_CODE_CHALLENGE);
            String codeChallengeMethod = requestParameters.get(KEY_CODE_CHALLENGE_METHOD);
            return new PkceProtectedAuthentication(codeChallenge, codeChallengeMethod, authentication);
        } else {
            // client 未开启pkce流程,通过pkce封装为默认oauth
            return new PkceProtectedAuthentication(authentication);
        }
    }

    /**
     * 判定指定客户端是否支持pkce流程
     * 根据additionalInformation中的 KEY_ENABLE_PKCE键对应内容(布尔)进行判定
     *
     * @param clientId 客户端ID
     * @return true支持, false不支持
     */
    protected boolean isClientSupportPkce(String clientId) {
        ClientDetails client = clientDetailsService.loadClientByClientId(clientId);
        if (client == null) {
            return false;
        }
        return (Boolean) client.getAdditionalInformation().getOrDefault(KEY_ENABLE_PKCE, Boolean.FALSE);
    }
}
