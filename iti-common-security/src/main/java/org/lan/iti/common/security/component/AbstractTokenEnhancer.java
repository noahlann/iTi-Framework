/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.common.security.component;

import lombok.AllArgsConstructor;
import org.lan.iti.common.core.constants.SecurityConstants;
import org.lan.iti.common.security.model.ITIUserDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

/**
 * Token增强
 * <p>
 * 客户端模式不增强
 * </p>
 *
 * @author NorthLan
 * @date 2020-02-24
 * @url https://noahlan.com
 */
@AllArgsConstructor
public abstract class AbstractTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        if (SecurityConstants.CLIENT_CREDENTIALS
                .equals(authentication.getOAuth2Request().getGrantType())) {
            return accessToken;
        }
        final Map<String, Object> additionalInfo = new HashMap<>(8);
        additionalInfo.put(SecurityConstants.DETAILS_LICENSE, SecurityConstants.LICENSE);
        additionalInfo.put(SecurityConstants.ACTIVE, Boolean.TRUE);

        ITIUserDetails userDetails = (ITIUserDetails) authentication.getUserAuthentication().getPrincipal();
        userDetails.encryptSecret();
        
        additionalInfo.put(SecurityConstants.DETAILS_USER_DETAILS, userDetails);

        // 子类处理
        additional(additionalInfo, accessToken, authentication);

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }

    protected abstract void additional(Map<String, Object> map, OAuth2AccessToken accessToken, OAuth2Authentication authentication);
}
