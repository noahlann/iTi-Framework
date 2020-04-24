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
import org.lan.iti.common.security.social.connect.ConnectionFactoryLocator;
import org.lan.iti.common.security.social.connect.UsersConnectionRepository;
import org.lan.iti.common.security.social.security.SocialAuthenticationFilter;
import org.lan.iti.common.security.social.security.SocialAuthenticationProvider;
import org.lan.iti.common.security.social.security.SocialAuthenticationServiceLocator;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

/**
 * Configuration class imported by {@link EnableITISocial}.
 *
 * @author NorthLan
 * @date 2020-03-21
 * @url https://noahlan.com
 */
public class SocialConfiguration extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private List<SocialConfigurer> socialConfigurers;

    @Override
    public void configure(HttpSecurity builder) {
        ApplicationContext applicationContext = builder.getSharedObject(ApplicationContext.class);
        UsersConnectionRepository usersConnectionRepository = getBean(applicationContext, UsersConnectionRepository.class);
        SocialAuthenticationServiceLocator authServiceLocator = getBean(applicationContext, SocialAuthenticationServiceLocator.class);
        ITIUserDetailsService userDetailsService = getBean(applicationContext, ITIUserDetailsService.class);

        SocialAuthenticationFilter filter = new SocialAuthenticationFilter(
                builder.getSharedObject(AuthenticationManager.class),
                usersConnectionRepository,
                authServiceLocator);

        RememberMeServices rememberMeServices = builder.getSharedObject(RememberMeServices.class);
        Optional.ofNullable(rememberMeServices).ifPresent(filter::setRememberMeServices);

        builder.authenticationProvider(new SocialAuthenticationProvider(usersConnectionRepository, userDetailsService))
                .addFilterBefore(postProcess(filter), AbstractPreAuthenticatedProcessingFilter.class);
    }

    public void setSocialConfigurers(List<SocialConfigurer> socialConfigurers) {
        Assert.notNull(socialConfigurers, "At least one configuration class must implement SocialConfigurer (or subclass SocialConfigurerAdapter)");
        Assert.notEmpty(socialConfigurers, "At least one configuration class must implement SocialConfigurer (or subclass SocialConfigurerAdapter)");
        this.socialConfigurers = socialConfigurers;
    }

    @Bean
    public ConnectionFactoryLocator connectionFactoryLocator() {
        SecurityConnectionFactoryConfigurer configurer = new SecurityConnectionFactoryConfigurer();
        for (SocialConfigurer it : socialConfigurers) {
            it.addConnectionFactories(configurer);
        }
        return configurer.getConnectionFactoryLocator();
    }

    @Bean
    public UsersConnectionRepository usersConnectionService(ConnectionFactoryLocator connectionFactoryLocator) {
        UsersConnectionRepository usersConnectionRepository = null;
        for (SocialConfigurer socialConfigurer : socialConfigurers) {
            UsersConnectionRepository repo = socialConfigurer.getUsersConnectionRepository(connectionFactoryLocator);
            if (repo != null) {
                usersConnectionRepository = repo;
                break;
            }
        }
        Assert.notNull(usersConnectionRepository, "One configuration class must implement getUsersConnectionRepository from SocialConfigurer.");
        return usersConnectionRepository;
    }

//    @Bean
//    @Scope(value="request", proxyMode= ScopedProxyMode.INTERFACES)
//    public ConnectionRepository connectionRepository(UsersConnectionRepository usersConnectionRepository) {
//        return usersConnectionRepository.createConnectionRepository(userIdSource().getUserId());
//    }

    private <T> T getBean(ApplicationContext applicationContext, Class<T> beanType) {
        try {
            return applicationContext.getBean(beanType);
        } catch (NoSuchBeanDefinitionException e) {
            throw new IllegalStateException("ITISocialConfigurer depends on " + beanType.getName() + ". No single bean of that type found in application context.", e);
        }
    }
}
