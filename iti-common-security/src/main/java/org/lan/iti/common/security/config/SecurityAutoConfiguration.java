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

package org.lan.iti.common.security.config;

import org.lan.iti.common.security.component.DefaultTokenEnhancer;
import org.lan.iti.common.security.component.SecurityExceptionHandler;
import org.lan.iti.common.security.service.DefaultUserDetailsBuilder;
import org.lan.iti.common.security.service.UserDetailsBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

/**
 * 安全模块自动装配
 *
 * @author NorthLan
 * @date 2020-04-10
 * @url https://noahlan.com
 */
@Configuration
@ConditionalOnClass(WebMvcAutoConfiguration.class)
public class SecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SecurityExceptionHandler securityExceptionHandler() {
        return new SecurityExceptionHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public TokenEnhancer itiTokenEnhancer() {
        return new DefaultTokenEnhancer();
    }

    @Bean
    @ConditionalOnMissingBean
    public UserDetailsBuilder userDetailsBuilder() {
        return new DefaultUserDetailsBuilder();
    }

    @Bean
    @ConditionalOnMissingBean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
