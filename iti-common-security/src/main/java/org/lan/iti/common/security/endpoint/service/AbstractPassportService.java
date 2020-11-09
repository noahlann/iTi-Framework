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

package org.lan.iti.common.security.endpoint.service;

import cn.hutool.core.util.StrUtil;
import org.lan.iti.common.core.exception.ServiceException;
import org.lan.iti.common.security.endpoint.constants.PassportConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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

    @Override
    public Map<String, String> grant(Map<String, String> params) {
        Map<String, Object> token = innerGrant(params);
        if (token == null) {
            throw new ServiceException(1000, "登录失败");
        }
        // filter & caching
        Map<String, String> result = filterToken(token);
        // caching
        cacheRefreshToken(token.get(PassportConstants.ACCESS_TOKEN).toString(),
                token.get(PassportConstants.REFRESH_TOKEN).toString(),
                Long.parseLong(token.get(PassportConstants.EXPIRES_IN).toString()));
        return result;
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
     * @param accessToken  access_token
     * @param refreshToken refresh_token
     * @param expiresIn    过期时间(单位: 秒 | s)
     */
    protected void cacheRefreshToken(String accessToken, String refreshToken, Long expiresIn) {
        // 缓存 REFRESH_TOKEN
        redisTemplate.opsForValue().set(
                PassportConstants.KEY_CACHE_PREFIX + PassportConstants.KEY_ACCESS_TO_REFRESH + accessToken,
                refreshToken,
                Duration.ofSeconds(expiresIn));
    }
}
