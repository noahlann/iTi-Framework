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

package org.lan.iti.cloud.autoconfigure.security;

import lombok.AllArgsConstructor;
import org.lan.iti.cloud.security.context.SecurityContextPersistenceFilter;
import org.lan.iti.cloud.security.http.AcceptRequestInterceptor;
import org.lan.iti.cloud.security.properties.SecurityProperties;
import org.lan.iti.cloud.security.web.FilterChainProxy;
import org.lan.iti.iha.security.web.SecurityFilterChain;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全模块自动装配
 *
 * @author NorthLan
 * @date 2020-04-10
 * @url https://noahlan.com
 */
@Configuration
@ConditionalOnClass({
        WebMvcAutoConfiguration.class,
        PasswordEncoder.class,
        ClientHttpRequestInterceptor.class,
        SecurityContextPersistenceFilter.class,
        FilterChainProxy.class})
@EnableConfigurationProperties(SecurityProperties.class)
@AllArgsConstructor
public class SecurityAutoConfiguration {

    @Bean
    public AcceptRequestInterceptor acceptRequestInterceptor() {
        return new AcceptRequestInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityContextPersistenceFilter securityContextPersistenceFilter() {
        return new SecurityContextPersistenceFilter();
    }

    @Bean
    public FilterChainProxy filterChainProxy(ObjectProvider<List<SecurityFilterChain>> filterProvider) {
        List<SecurityFilterChain> chains = filterProvider.getIfAvailable();
        if (chains == null) {
            chains = new ArrayList<>();
        }
        return new FilterChainProxy(chains);
    }

}
