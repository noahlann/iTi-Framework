/*
 *
 *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.common.core.autoconfigure;

import lombok.AllArgsConstructor;
import org.lan.iti.common.core.properties.MultipartProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.MultipartConfigElement;
import java.io.IOException;

/**
 * @author NorthLan
 * @date 2020-04-30
 * @url https://noahlan.com
 */
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "iti.multipart", name = "enabled", matchIfMissing = true)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(MultipartProperties.class)
public class ITIMultipartAutoConfiguration {
    private final MultipartProperties properties;

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public MultipartConfigElement multipartConfigElement() {
        return this.properties.createMultipartConfig();
    }

    /**
     * CommonsMultipartResolver
     */
    @Bean(name = DispatcherServlet.MULTIPART_RESOLVER_BEAN_NAME)
    @Primary
    public CommonsMultipartResolver multipartResolver() throws IOException {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setResolveLazily(properties.isResolveLazily());
        resolver.setDefaultEncoding(properties.getDefaultEncoding());
        resolver.setMaxInMemorySize((int) properties.getMaxInMemorySize().toBytes());
        resolver.setMaxUploadSize(properties.getMaxUploadSize().toBytes());
        resolver.setMaxUploadSizePerFile(properties.getMaxUploadSizePerFile().toBytes());
        if (properties.getUploadTempDir() != null) {
            resolver.setUploadTempDir(properties.getUploadTempDir());
        }
        return resolver;
    }
}
