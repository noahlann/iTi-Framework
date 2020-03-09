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

package org.lan.iti.common.gateway.configuration;

import com.alibaba.cloud.nacos.NacosConfigAutoConfiguration;
import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import org.lan.iti.common.gateway.support.nacos.NacosDynamicRouteEvent;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.gateway.config.GatewayAutoConfiguration;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Nacos动态路由自动装配
 *
 * @author NorthLan
 * @date 2020-03-09
 * @url https://noahlan.com
 */
@Configuration
@AutoConfigureAfter({NacosConfigAutoConfiguration.class, GatewayAutoConfiguration.class})
public class NacosDynamicRouteAutoConfiguration {

    // region 基于NacosConfigServer的动态路由
    @Bean
    @ConditionalOnMissingBean
    public NacosDynamicRouteEvent nacosDynamicRouteEvent(RouteDefinitionWriter routeDefinitionWriter,
                                                         NacosConfigProperties nacosConfigProperties,
                                                         NacosConfigManager nacosConfigManager) {
        return new NacosDynamicRouteEvent(routeDefinitionWriter, nacosConfigProperties, nacosConfigManager);
    }
    // endregion
}
