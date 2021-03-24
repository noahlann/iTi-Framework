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

package org.lan.iti.cloud.autoconfigure.gateway;

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.gateway.exception.RouteCheckException;
import org.lan.iti.cloud.gateway.support.DynamicRouteHealthIndicator;
import org.lan.iti.cloud.gateway.support.RedisRouteDefinitionRepository;
import org.lan.iti.cloud.gateway.support.RouteCacheHolder;
import org.lan.iti.cloud.support.SpringContextHolder;
import org.lan.iti.common.core.constants.CacheConstants;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.config.PropertiesRouteDefinitionLocator;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 动态路由配置类
 *
 * @author NorthLan
 * @date 2021-03-10
 * @url https://noahlan.com
 */
@SuppressWarnings("unchecked")
@Slf4j
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@AutoConfigureBefore(GatewayAutoConfiguration.class)
public class DynamicRouteAutoConfiguration {

    /**
     * 配置文件设置为空，使用Redis加载为准
     *
     * @return 空配置文件
     */
    @Bean
    public PropertiesRouteDefinitionLocator propertiesRouteDefinitionLocator() {
        return new PropertiesRouteDefinitionLocator(new GatewayProperties());
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener((messages, bytes) -> {
            log.info("接收到 重新加载路由 事件");
            RouteCacheHolder.removeRouteList();
            // 发送路由刷新事件
            SpringContextHolder.publishEvent(new RefreshRoutesEvent(this));
        }, new ChannelTopic(CacheConstants.ROUTE_JVM_RELOAD_TOPIC));
        return container;
    }

    @Bean
    public DynamicRouteHealthIndicator healthIndicator(RedisTemplate redisTemplate) {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        if (!redisTemplate.hasKey(CacheConstants.ROUTE_KEY)) {
            throw new RouteCheckException("路由信息未能初始化, 网关启动失败");
        }
        return new DynamicRouteHealthIndicator(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public RouteDefinitionRepository routeDefinitionRepository(RedisTemplate redisTemplate) {
        return new RedisRouteDefinitionRepository(redisTemplate);
    }
}
