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

package org.lan.iti.common.scanner;

import lombok.AllArgsConstructor;
import org.lan.iti.common.scanner.listener.ResourceReportListener;
import org.lan.iti.common.scanner.properties.ScannerProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 扫描器自动配置
 * <p>
 * 注意：资源扫描的使用 需要配置IResourceService这个Bean到Spring容器
 *
 * @author NorthLan
 * @date 2020-03-06
 * @url https://noahlan.com
 */
@Configuration
@RefreshScope
@EnableConfigurationProperties(ScannerProperties.class)
@AllArgsConstructor
public class ScannerAutoConfiguration {
    private ScannerProperties properties;

    @Bean
    @ConditionalOnProperty(name = "iti.scanner.report.enabled", havingValue = "true")
    ResourceReportListener resourceReportListener() {
        return new ResourceReportListener(properties);
    }

    @Bean
    ApiResourceScanner apiResourceScanner() {
        return new ApiResourceScanner(properties);
    }
}
