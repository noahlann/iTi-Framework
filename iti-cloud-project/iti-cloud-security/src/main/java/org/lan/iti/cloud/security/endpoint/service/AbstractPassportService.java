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

package org.lan.iti.cloud.security.endpoint.service;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.lan.iti.cloud.security.endpoint.constants.PassportConstants;
import org.lan.iti.cloud.security.model.ITIUserDetails;
import org.lan.iti.cloud.security.util.SecurityUtils;
import org.lan.iti.common.core.constants.SecurityConstants;
import org.lan.iti.common.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 身份服务基类
 *
 * @author NorthLan
 * @date 2020-11-09
 * @url https://noahlan.com
 */
public abstract class AbstractPassportService implements PassportService {
    @Autowired
    protected RedisTemplate<String, String> redisTemplate;

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    protected OAuth2ProtectedResourceDetails protectedResourceDetails;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public Map<String, String> grant(Map<String, String> params) {
        Map<String, Object> token = innerGrant(params);
        if (token == null) {
            throw new ServiceException(1000, "登录失败");
        }
        return transferToken(token);
    }

    private Map<String, String> transferToken(Map<String, Object> token) {
        // filter & caching
        Map<String, String> result = filterToken(token);
        // caching
        ITIUserDetails userDetails;
        try {
            userDetails = objectMapper.convertValue(token.get(SecurityConstants.DETAILS_USER_DETAILS), ITIUserDetails.class);
        } catch (IllegalArgumentException e) {
            throw new ServiceException(1000, "登录失败，无法获取用户基本信息");
        }
        cacheRefreshToken(userDetails.getUserId(),
                token.get(PassportConstants.REFRESH_TOKEN).toString(),
                Long.parseLong(token.get(PassportConstants.EXPIRES_IN).toString()));
        return result;
    }

    @Override
    public Map<String, String> refreshToken() {
        ITIUserDetails user = SecurityUtils.getUser()
                .orElseThrow(() -> new ServiceException(1000, "无法刷新访问凭证，请确认当前已正常登录"));
        // 从缓存中获取
        String cacheKey = cacheKey(user.getUserId());
        String refreshToken = redisTemplate.opsForValue().get(cacheKey);
        if (StrUtil.isBlank(refreshToken)) {
            throw new ServiceException(1001, "Token已彻底过期，无法自动刷新。");
        }

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.set(PassportConstants.CLIENT_ID, protectedResourceDetails.getClientId());
        map.set(PassportConstants.CLIENT_SECRET, protectedResourceDetails.getClientSecret());
        map.set(PassportConstants.GRANT_TYPE, PassportConstants.REFRESH_TOKEN);
        map.set(PassportConstants.REFRESH_TOKEN, refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map, headers);

        // 仅允许使用一次refresh_token，无论成功失败
        redisTemplate.delete(cacheKey);

        Map<String, Object> newToken = restTemplate.postForObject(protectedResourceDetails.getAccessTokenUri(),
                requestEntity, Map.class);
        if (newToken == null) {
            throw new ServiceException(1002, "刷新Token失败，请联系管理员。");
        }
        return transferToken(newToken);
    }

    protected abstract Map<String, Object> innerGrant(Map<String, String> params);

    /**
     * 过滤Token,保证安全性
     * <p>
     * 当Token中不存在keys指定字段时,取空字符串
     * </p>
     *
     * @param token 原始Token
     * @return 过滤后的Token
     */
    protected Map<String, String> filterToken(Map<String, Object> token) {
        Map<String, String> result = new HashMap<>();
        PassportConstants.RETURN_KEYS.forEach(it -> result.put(it, token.getOrDefault(it, StrUtil.EMPTY).toString()));
        return result;
    }

    /**
     * 缓存 access_token:refresh_token
     *
     * @param userId       用户唯一ID
     * @param refreshToken refresh_token
     * @param expiresIn    过期时间(单位: 秒 | s)
     */
    protected void cacheRefreshToken(String userId, String refreshToken, Long expiresIn) {
        // 缓存 REFRESH_TOKEN
        redisTemplate.opsForValue().set(
                cacheKey(userId),
                refreshToken,
                Duration.ofSeconds(expiresIn));
    }

    private String cacheKey(String userId) {
        return PassportConstants.KEY_CACHE_PREFIX + PassportConstants.KEY_ACCESS_TO_REFRESH + userId;
    }
}
