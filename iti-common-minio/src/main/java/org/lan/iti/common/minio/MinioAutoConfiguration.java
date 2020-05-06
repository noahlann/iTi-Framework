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

package org.lan.iti.common.minio;

import lombok.AllArgsConstructor;
import org.lan.iti.common.minio.component.AutoCreateBucketConfiguration;
import org.lan.iti.common.minio.http.MinioEndpoint;
import org.lan.iti.common.minio.service.MinioTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * minio 自动配置类
 *
 * @author NorthLan
 * @date 2020-03-04
 * @url https://noahlan.com
 */
@Configuration
@AllArgsConstructor
@EnableConfigurationProperties({MinioProperties.class})
public class MinioAutoConfiguration {
    private final MinioProperties properties;

    @Bean
    @ConditionalOnMissingBean(MinioTemplate.class)
    @ConditionalOnProperty(name = "iti.minio.url")
    public MinioTemplate minioTemplate() {
        return new MinioTemplate(
                properties.getUrl(),
                properties.getAccessKey(),
                properties.getSecretKey()
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public AutoCreateBucketConfiguration autoCreateBucketConfiguration() {
        return new AutoCreateBucketConfiguration(properties, minioTemplate());
    }

    @Bean
    @ConditionalOnBean(MinioTemplate.class)
    @ConditionalOnProperty(name = "iti.minio.endpoint.enabled", havingValue = "true")
    public MinioEndpoint minioEndpoint() {
        return new MinioEndpoint(minioTemplate());
    }
}
