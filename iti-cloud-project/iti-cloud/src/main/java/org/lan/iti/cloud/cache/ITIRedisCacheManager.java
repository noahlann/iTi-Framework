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

package org.lan.iti.cloud.cache;

import cn.hutool.core.util.StrUtil;
import org.lan.iti.cloud.tenant.TenantContextHolder;
import org.lan.iti.common.core.constants.CacheConstants;
import org.lan.iti.common.core.util.StringPool;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * 自定义CacheManager
 * <p>
 * 1. 支持针对不同的缓存空间(即 cacheName)设定不同的 TTL
 * example: @Cacheable(value = ["cacheName#ttl"]) ttl->超时时间(s)
 * 2. 多租户支持
 * </p>
 *
 * @author NorthLan
 * @date 2020-02-24
 * @url https://noahlan.com
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
public class ITIRedisCacheManager extends RedisCacheManager {
    private static final String SPLIT_FLAG = "#";
    private static final int CACHE_LENGTH = 2;

    public ITIRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration, Map<String, RedisCacheConfiguration> initialCacheConfigurations, boolean allowInFlightCacheCreation) {
        super(cacheWriter, defaultCacheConfiguration, initialCacheConfigurations, allowInFlightCacheCreation);
    }

    @NonNull
    @Override
    protected RedisCache createRedisCache(@NonNull String name, @Nullable RedisCacheConfiguration cacheConfig) {
        // # 过滤
        if (StrUtil.isBlank(name) || !name.contains(SPLIT_FLAG)) {
            return super.createRedisCache(name, cacheConfig);
        }
        String[] cacheArray = name.split(SPLIT_FLAG);
        if (cacheArray.length < CACHE_LENGTH) {
            return super.createRedisCache(name, cacheConfig);
        }
        if (cacheConfig != null) {
            Duration duration = DurationStyle.detectAndParse(cacheArray[1], ChronoUnit.SECONDS);
            cacheConfig = cacheConfig.entryTtl(duration);
        }
        return super.createRedisCache(cacheArray[0], cacheConfig);
    }


    @Override
    @Nullable
    public Cache getCache(String name) {
        if (name.startsWith(CacheConstants.GLOBAL_KEY)) {
            return super.getCache(name);
        }
        return super.getCache(TenantContextHolder.getTenantId() + StringPool.COLON + name);
    }
}
