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

package org.lan.iti.cloud.gateway.support;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.gateway.vo.RouteDefinitionVo;
import org.lan.iti.common.core.constants.CacheConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 基于Redis的路由缓存
 *
 * @author NorthLan
 * @date 2021-03-10
 * @url https://noahlan.com
 */
@Slf4j
@RequiredArgsConstructor
public class RedisRouteDefinitionRepository implements RouteDefinitionRepository {
    private final RedisTemplate redisTemplate;

    /**
     * 1. 先从内存中获取 2. 为空加载Redis中数据 3. 更新内存
     *
     * @return 路由
     */
    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        List<RouteDefinitionVo> routeList = RouteCacheHolder.getRouteList();
        if (CollUtil.isNotEmpty(routeList)) {
            log.debug("内存 中路由定义条数： {}， {}", routeList.size(), routeList);
            return Flux.fromIterable(routeList);
        }

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(RouteDefinitionVo.class));
        List<RouteDefinitionVo> values = redisTemplate.opsForHash().values(CacheConstants.ROUTE_KEY);
        log.debug("redis 中路由定义条数： {}， {}", values.size(), values);

        RouteCacheHolder.setRouteList(values);
        return Flux.fromIterable(values);
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return route.flatMap(r -> {
            RouteDefinitionVo vo = new RouteDefinitionVo();
            BeanUtils.copyProperties(r, vo);
            log.info("保存路由信息{}", vo);
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.opsForHash().put(CacheConstants.ROUTE_KEY, r.getId(), vo);
            redisTemplate.convertAndSend(CacheConstants.ROUTE_JVM_RELOAD_TOPIC, "新增路由信息,网关缓存更新");
            return Mono.empty();
        });
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        routeId.subscribe(id -> {
            log.info("删除路由信息{}", id);
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.opsForHash().delete(CacheConstants.ROUTE_KEY, id);
        });
        redisTemplate.convertAndSend(CacheConstants.ROUTE_JVM_RELOAD_TOPIC, "删除路由信息,网关缓存更新");
        return Mono.empty();
    }
}
