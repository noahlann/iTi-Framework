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

import org.lan.iti.common.security.service.ITIUserDetailsService;
import org.lan.iti.common.security.social.SocialAuthenticationProvider;
import org.lan.iti.common.security.social.service.SocialAuthenticationServiceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 社交登录Web配置
 *
 * @author NorthLan
 * @date 2020-05-25
 * @url https://noahlan.com
 */
@Order(100 + 10)
public class SocialSecurityConfiguration extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    @Autowired
    private ITIUserDetailsService userDetailsService;

    @Autowired
    private SocialAuthenticationServiceRegistry registry;

    private List<ITISocialConfigurer> socialConfigurers;

    @Autowired
    public void setSocialConfigurers(List<ITISocialConfigurer> socialConfigurers) {
        Assert.notNull(socialConfigurers, "At least one configuration class must implement SocialConfigurer (or subclass SocialConfigurerAdapter)");
        Assert.notEmpty(socialConfigurers, "At least one configuration class must implement SocialConfigurer (or subclass SocialConfigurerAdapter)");
        this.socialConfigurers = socialConfigurers;
    }

    @Override
    public void configure(HttpSecurity http) {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        for (ITISocialConfigurer configurer : socialConfigurers) {
            configurer.setAuthenticationManager(authenticationManager);
            configurer.addAuthenticationServices(registry);
        }
        SocialAuthenticationProvider provider = new SocialAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setRegistry(registry);
        http.authenticationProvider(provider);
    }
}
