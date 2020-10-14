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

package org.lan.iti.common.security.endpoint;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.model.response.ApiResult;
import org.lan.iti.common.security.annotation.Inner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * sso端点
 * 使用OAuth2协议 授权码模式
 *
 * @author NorthLan
 * @date 2020-08-03
 * @url https://noahlan.com
 */
@Slf4j
@RestController
@RequestMapping("sso/v1")
public class SsoEndpoint {
    private final static String REDIRECT_URI = "redirect_uri";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AuthorizationCodeResourceDetails details;

    @GetMapping("login")
    @Inner(false)

    public ApiResult<?> getLogin(@RequestParam Map<String, String> params) {
        return postLogin(params);
    }

    @PostMapping("login")
    @Inner(false)
    public ApiResult<?> postLogin(@RequestParam Map<String, String> params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.set("client_id", details.getClientId());
        map.set("client_secret", details.getClientSecret());
        map.set("grant_type", details.getGrantType());
        String redirectUri;
        if (details.isUseCurrentUri()) {
            // 优先使用请求中的 redirect_uri
            redirectUri = params.remove(REDIRECT_URI);
            if (StrUtil.isBlank(redirectUri)) {
                redirectUri = details.getPreEstablishedRedirectUri();
            }
        } else {
            params.remove(REDIRECT_URI);
            redirectUri = details.getPreEstablishedRedirectUri();
        }
        if (StrUtil.isNotBlank(redirectUri)) {
            map.set(REDIRECT_URI, redirectUri);
        }

        params.forEach(map::set);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);

        Map<String, Object> token = restTemplate.postForObject(details.getAccessTokenUri(),
                requestEntity, Map.class);

        return ApiResult.ok(token);
    }
}
