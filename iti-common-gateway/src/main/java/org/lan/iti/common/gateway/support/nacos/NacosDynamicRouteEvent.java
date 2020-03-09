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

package org.lan.iti.common.gateway.support.nacos;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.gateway.model.RouteDefinitionVo;
import org.lan.iti.common.gateway.support.RouteCacheHolder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * 基于Nacos的动态路由事件处理器
 *
 * @author NorthLan
 * @date 2020-03-09
 * @url https://noahlan.com
 */
@Slf4j
public class NacosDynamicRouteEvent implements ApplicationListener<ApplicationReadyEvent>, ApplicationEventPublisherAware {
    private static final String DEFAULT_DATA_ID = "iti-gateway-router";

    private ApplicationEventPublisher applicationEventPublisher;
    private RouteDefinitionWriter routeDefinitionWriter;
    private NacosConfigProperties nacosConfigProperties;
    private NacosConfigManager nacosConfigManager;

    public NacosDynamicRouteEvent(RouteDefinitionWriter routeDefinitionWriter, NacosConfigProperties nacosConfigProperties, NacosConfigManager nacosConfigManager) {
        this.routeDefinitionWriter = routeDefinitionWriter;
        this.nacosConfigProperties = nacosConfigProperties;
        this.nacosConfigManager = nacosConfigManager;
    }

    @Override
    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        try {
            ConfigService configService = nacosConfigManager.getConfigService();
            String dataId = DEFAULT_DATA_ID;
            String content = configService.getConfigAndSignListener(dataId, nacosConfigProperties.getGroup(), nacosConfigProperties.getTimeout(),
                    new Listener() {
                        @Override
                        public Executor getExecutor() {
                            return null;
                        }

                        @Override
                        public void receiveConfigInfo(String s) {
                            setRouteInfo(s);
                        }
                    });
            // 第一次SignListener时,不会触发receiveConfigInfo
            setRouteInfo(content);
        } catch (NacosException e) {
            log.error("nacos 异常，无法从nacos config server加载动态路由", e);
        }
    }

    /**
     * 设置路由信息
     *
     * @param content 读取到的路由json数据
     */
    private void setRouteInfo(String content) {
        clearRoute();
        List<RouteDefinitionVo> routeDefinitions = JSONObject.parseArray(content, RouteDefinitionVo.class);
        if (routeDefinitions != null) {
            if (log.isDebugEnabled()) {
                log.debug("添加路由信息,共计: {} 条", routeDefinitions.size());
            }
            routeDefinitions.forEach(this::addRoute);
            if (log.isDebugEnabled()) {
                log.debug("添加路由完成...");
            }
            publishEvent();
        }
    }

    /**
     * 清理路由信息
     */
    private void clearRoute() {
        if (log.isDebugEnabled()) {
            log.debug("清理路由信息,共计: {} 条", RouteCacheHolder.size());
        }
        RouteCacheHolder.getRouteList()
                .forEach(it -> routeDefinitionWriter.delete(Mono.just(it.getId())).subscribe());
        RouteCacheHolder.clear();

        if (log.isDebugEnabled()) {
            log.debug("清理路由信息完成...");
        }
    }

    /**
     * 添加路由信息
     * <p>
     * 添加Vo到writer中
     * 添加Id到List中
     * </p>
     *
     * @param definitionVo 路由信息
     */
    private void addRoute(RouteDefinitionVo definitionVo) {
        routeDefinitionWriter.save(Mono.just(definitionVo)).subscribe();
        RouteCacheHolder.add(definitionVo);
    }

    /**
     * 发布路由刷新事件
     */
    private void publishEvent() {
        this.applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this.routeDefinitionWriter));
    }
}
