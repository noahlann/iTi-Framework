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

package org.lan.iti.common.security.social.config;

import lombok.RequiredArgsConstructor;
import org.lan.iti.common.security.service.ITIUserDetailsService;
import org.lan.iti.common.security.social.SocialAuthenticationProvider;
import org.lan.iti.common.security.social.service.SocialAuthenticationServiceConfigurer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;

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
@Order(2)
public class SocialSecurityConfiguration extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final ITIUserDetailsService userDetailsService;
    private final ClientDetailsService clientDetailsService;
    private final ApplicationContext applicationContext;
    private final ObjectProvider<AuthenticationManager> authenticationManager;

    private final SocialAuthenticationServiceConfigurer configurer = new SocialAuthenticationServiceConfigurer();

    @Autowired
    private List<ITISocialAdapter> socialAdapters = Collections.emptyList();

    @PostConstruct
    public void init() throws Exception {
        configurer.setApplicationContext(applicationContext);
        configurer.setAuthenticationManager(authenticationManager.getIfAvailable());
        configurer.setClientDetailsService(clientDetailsService);

        for (ITISocialAdapter socialAdapter : socialAdapters) {
            socialAdapter.configure(configurer);
        }
    }

    @Override
    public void configure(HttpSecurity http) {
        SocialAuthenticationProvider provider = new SocialAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setRegistry(configurer);
        http.authenticationProvider(provider);
    }

    public SocialAuthenticationServiceConfigurer getSocialConfigurer() {
        return this.configurer;
    }
}
