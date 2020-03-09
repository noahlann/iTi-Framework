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

package org.lan.iti.common.gateway.support.redis;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.constants.CacheConstants;
import org.lan.iti.common.gateway.model.RouteDefinitionVo;
import org.lan.iti.common.gateway.support.RouteCacheHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

/**
 * 基于Redis的路由信息仓库
 *
 * @author NorthLan
 * @date 2020-03-09
 * @url https://noahlan.com
 */
@Slf4j
@AllArgsConstructor
public class RedisRouteDefinitionRepository implements RouteDefinitionRepository {
    private final RedisTemplate<String, RouteDefinitionVo> redisTemplate;

    private void setupRedisTemplate() {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(RouteDefinitionVo.class));
    }

    /**
     * 获取动态路由
     * <p>
     * 1. 读内存
     * 2. 读Redis
     * 3. 从Redis更新至内存
     * </p>
     */
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        // 读内存
        Collection<RouteDefinitionVo> inMemoryRouteList = RouteCacheHolder.getRouteList();
        if (CollUtil.isNotEmpty(inMemoryRouteList)) {
            log.debug("内存中缓存路由：{} {}", inMemoryRouteList.size(), inMemoryRouteList);
            return Flux.fromIterable(inMemoryRouteList);
        }
        // 读Redis
        setupRedisTemplate();
        List<RouteDefinitionVo> redisRouteList = redisTemplate.<String, RouteDefinitionVo>opsForHash().values(CacheConstants.ROUTE_KEY);

        log.debug("Redis中缓存路由：{} {}", redisRouteList.size(), redisRouteList);

        // 更新内存缓存
        RouteCacheHolder.clear();
        RouteCacheHolder.add(redisRouteList);
        return Flux.fromIterable(redisRouteList);
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route.flatMap(r -> {
            RouteDefinitionVo vo = new RouteDefinitionVo();
            BeanUtils.copyProperties(r, vo);
            log.debug("保存路由信息: {}", vo);

            setupRedisTemplate();
            redisTemplate.<String, RouteDefinitionVo>opsForHash().put(CacheConstants.ROUTE_KEY, vo.getId(), vo);
            redisTemplate.convertAndSend(CacheConstants.ROUTE_JVM_RELOAD_TOPIC, "新增路由信息,网关缓存更新");
            return Mono.empty();
        });
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return routeId.flatMap(id -> {
            log.debug("删除路由信息: {}", id);
            setupRedisTemplate();
            redisTemplate.<String, RouteDefinitionVo>opsForHash().delete(CacheConstants.ROUTE_KEY, id);
            redisTemplate.convertAndSend(CacheConstants.ROUTE_JVM_RELOAD_TOPIC, "删除路由信息,网关缓存更新");
            return Mono.empty();
        });
    }
}
