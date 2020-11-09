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

import org.lan.iti.common.security.endpoint.constants.PassportConstants;
import org.lan.iti.common.security.endpoint.service.AbstractPassportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

/**
 * 用户名密码登录
 *
 * @author NorthLan
 * @date 2020-11-09
 * @url https://noahlan.com
 */
@Service(PassportConstants.PREFIX_PASSPORT_SERVICE + UsernamePasswordPassportService.GRANT_TYPE)
public class UsernamePasswordPassportService extends AbstractPassportService {
    public static final String GRANT_TYPE = "password";

    @Autowired
    private ResourceOwnerPasswordResourceDetails details;

    @SuppressWarnings("unchecked")
    @Override
    protected Map<String, Object> innerGrant(Map<String, String> params) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.set(PassportConstants.CLIENT_ID, details.getClientId());
        map.set(PassportConstants.CLIENT_SECRET, details.getClientSecret());
        map.set(PassportConstants.GRANT_TYPE, GRANT_TYPE);

        // set
        params.forEach(map::set);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);

        return restTemplate.postForObject(details.getAccessTokenUri(),
                requestEntity, Map.class);
    }
}
