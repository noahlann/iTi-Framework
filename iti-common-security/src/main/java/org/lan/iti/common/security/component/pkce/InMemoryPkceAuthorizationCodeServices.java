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

package org.lan.iti.common.security.component.pkce;

import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的授权码生成与消费服务,默认生成32位授权码
 * 支持 PKCE 流程
 *
 * @author NorthLan
 * @date 2020-08-06
 * @url https://noahlan.com
 */
@AllArgsConstructor
public class InMemoryPkceAuthorizationCodeServices extends PkceRandomValueAuthorizationCodeServices {
    // 缓存
    private final Map<String, PkceProtectedAuthentication> authorizationCodeStore = new ConcurrentHashMap<>();

    // 客户端服务
    private final ClientDetailsService clientDetailsService;

    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        authorizationCodeStore.put(code, getProtectedAuthentication(authentication));
    }

    @Override
    protected OAuth2Authentication remove(String code, String codeVerifier) {
        // code仅允许使用一次
        PkceProtectedAuthentication auth = authorizationCodeStore.remove(code);
        return auth.getOAuth2Authentication(codeVerifier);
    }


    private PkceProtectedAuthentication getProtectedAuthentication(OAuth2Authentication authentication) {
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
    private boolean isClientSupportPkce(String clientId) {
        ClientDetails client = clientDetailsService.loadClientByClientId(clientId);
        if (client == null) {
            return false;
        }
        return (Boolean) client.getAdditionalInformation().getOrDefault(KEY_ENABLE_PKCE, Boolean.FALSE);
    }
}
