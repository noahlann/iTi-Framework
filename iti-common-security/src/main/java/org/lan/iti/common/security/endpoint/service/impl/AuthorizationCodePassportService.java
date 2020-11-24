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

package org.lan.iti.common.security.endpoint.service.impl;

import cn.hutool.core.util.StrUtil;
import org.lan.iti.common.security.endpoint.constants.PassportConstants;
import org.lan.iti.common.security.endpoint.service.AbstractPassportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

import static org.lan.iti.common.security.endpoint.constants.PassportConstants.PREFIX_PASSPORT_SERVICE;

/**
 * Token 服务（内部使用）
 *
 * @author NorthLan
 * @date 2020-11-09
 * @url https://noahlan.com
 */
@Component(PREFIX_PASSPORT_SERVICE + AuthorizationCodePassportService.GRANT_TYPE)
public class AuthorizationCodePassportService extends AbstractPassportService {
    public static final String GRANT_TYPE = "authorization_code";

    @Autowired
    private AuthorizationCodeResourceDetails details;

    @SuppressWarnings("unchecked")
    @Override
    protected Map<String, Object> innerGrant(Map<String, String> params) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.set(PassportConstants.CLIENT_ID, protectedResourceDetails.getClientId());
        map.set(PassportConstants.CLIENT_SECRET, protectedResourceDetails.getClientSecret());
        map.set(PassportConstants.GRANT_TYPE, details.getGrantType());
        String redirectUri;
        if (details.isUseCurrentUri()) {
            // 优先使用请求中的 redirect_uri
            redirectUri = params.remove(PassportConstants.REDIRECT_URI);
            if (StrUtil.isBlank(redirectUri)) {
                redirectUri = details.getPreEstablishedRedirectUri();
            }
        } else {
            params.remove(PassportConstants.REDIRECT_URI);
            redirectUri = details.getPreEstablishedRedirectUri();
        }
        if (StrUtil.isNotBlank(redirectUri)) {
            map.set(PassportConstants.REDIRECT_URI, redirectUri);
        }

        params.forEach(map::set);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);

        return restTemplate.postForObject(protectedResourceDetails.getAccessTokenUri(),
                requestEntity, Map.class);
    }
}
