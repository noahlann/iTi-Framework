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

package org.lan.iti.cloud.security.social.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.lan.iti.cloud.security.component.ITICommenceAuthExceptionEntryPoint;
import org.lan.iti.cloud.security.service.ITIUserDetailsService;
import org.lan.iti.cloud.security.social.SocialAuthenticationFilter;
import org.lan.iti.cloud.security.social.SocialAuthenticationProvider;
import org.lan.iti.cloud.security.social.service.SocialAuthenticationServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

/**
 * 社交安全配置
 *
 * @author NorthLan
 * @date 2020-08-08
 * @url https://noahlan.com
 */
@Configuration
@RequiredArgsConstructor
public class SocialSecurityConfiguration extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final ITIUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;
    private final AuthenticationSuccessHandler socialLoginSuccessHandler;
    private final SocialAuthenticationServiceRegistry registry;

    @Autowired
    private List<ITISocialConfigurer> socialConfigurers = Collections.emptyList();

    @PostConstruct
    public void init() {
        for (ITISocialConfigurer socialConfigurer : socialConfigurers) {
            socialConfigurer.configure(registry);
        }
    }

    @Override
    public void configure(HttpSecurity http) {
        SocialAuthenticationFilter filter = new SocialAuthenticationFilter();
        filter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        filter.setAuthenticationSuccessHandler(socialLoginSuccessHandler);
        filter.setAuthenticationEntryPoint(new ITICommenceAuthExceptionEntryPoint(objectMapper));
        filter.setRegistry(registry);

        SocialAuthenticationProvider provider = new SocialAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setRegistry(registry);

        http.authenticationProvider(provider)
                .addFilterAfter(filter, UsernamePasswordAuthenticationFilter.class);
    }
}
