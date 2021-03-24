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

package org.lan.iti.cloud.autoconfigure.swagger;

import org.lan.iti.cloud.swagger.SwaggerProperties;
import org.lan.iti.cloud.swagger.support.CloudSwaggerProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Primary;
import springfox.documentation.spring.web.OnReactiveWebApplication;

/**
 * Gateway的聚合文档
 *
 * @author NorthLan
 * @date 2021-03-11
 * @url https://noahlan.com
 */
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@Conditional(OnReactiveWebApplication.class)
public class GatewaySwaggerAutoConfiguration {

    @Bean
    @Primary
    public CloudSwaggerProvider swaggerProvider(RouteDefinitionRepository routeDefinitionRepository,
                                                SwaggerProperties swaggerProperties,
                                                DiscoveryClient discoveryClient) {
        return new CloudSwaggerProvider(routeDefinitionRepository, swaggerProperties, discoveryClient);
    }
}
