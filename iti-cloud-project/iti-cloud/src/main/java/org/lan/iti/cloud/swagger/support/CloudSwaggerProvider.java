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

package org.lan.iti.cloud.swagger.support;

import lombok.RequiredArgsConstructor;
import org.lan.iti.cloud.swagger.SwaggerProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NameUtils;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基于Cloud-Discovery的聚合文档资源提供者
 *
 * @author NorthLan
 * @date 2021-03-11
 * @url https://noahlan.com
 */
@RequiredArgsConstructor
public class CloudSwaggerProvider implements SwaggerResourcesProvider {
    private static final String API_URI = "/v3/api-docs";

    private final RouteDefinitionRepository routeDefinitionRepository;
    private final SwaggerProperties swaggerProperties;
    private final DiscoveryClient discoveryClient;

    @Override
    public List<SwaggerResource> get() {
        List<RouteDefinition> routes = new ArrayList<>();
        routeDefinitionRepository.getRouteDefinitions()
                // swagger 显示服务名称根据 路由的order 字段进行升序排列
                .sort(Comparator.comparingInt(RouteDefinition::getOrder)).subscribe(routes::add);

        return routes.stream()
                .flatMap(routeDefinition -> routeDefinition.getPredicates().stream()
                        .filter(predicateDefinition -> "Path".equalsIgnoreCase(predicateDefinition.getName()))
                        .filter(predicateDefinition -> !swaggerProperties.getIgnoreProviders()
                                .contains(routeDefinition.getId()))
                        .map(predicateDefinition -> swaggerResource(routeDefinition.getId(),
                                predicateDefinition.getArgs().get(NameUtils.GENERATED_NAME_PREFIX + "0").replace("/**",
                                        API_URI))))
                // 只显示注册中心 内已经正确启动的服务
                .filter(swaggerResource -> discoveryClient.getServices().stream()
                        .anyMatch(s -> s.equals(swaggerResource.getName())))
                .collect(Collectors.toList());
    }

    private static SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("3.0");
        return swaggerResource;
    }
}
