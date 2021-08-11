///*
// *
// *  * Copyright (c) [2019-2021] [NorthLan](lan6995@gmail.com)
// *  *
// *  * Licensed under the Apache License, Version 2.0 (the "License");
// *  * you may not use this file except in compliance with the License.
// *  * You may obtain a copy of the License at
// *  *
// *  *     http://www.apache.org/licenses/LICENSE-2.0
// *  *
// *  * Unless required by applicable law or agreed to in writing, software
// *  * distributed under the License is distributed on an "AS IS" BASIS,
// *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  * See the License for the specific language governing permissions and
// *  * limitations under the License.
// *
// */
//
//package org.lan.iti.cloud.autoconfigure.security;
//
//import lombok.AllArgsConstructor;
//import org.lan.iti.cloud.security.component.DefaultTokenEnhancer;
//import org.lan.iti.cloud.security.component.ITISecurityInnerAspect;
//import org.lan.iti.cloud.security.component.PermitAllUrlResolver;
//import org.lan.iti.cloud.security.component.SecurityExceptionHandler;
//import org.lan.iti.cloud.security.interceptor.AcceptRequestInterceptor;
//import org.lan.iti.cloud.security.properties.SecurityProperties;
//import org.lan.iti.cloud.security.service.DefaultUserDetailsBuilder;
//import org.lan.iti.cloud.security.service.ITIUserDetailsServiceImpl;
//import org.lan.iti.cloud.security.service.UserDetailsBuilder;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.factory.PasswordEncoderFactories;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.provider.token.TokenEnhancer;
//import org.springframework.web.context.WebApplicationContext;
//
///**
// * 安全模块自动装配
// *
// * @author NorthLan
// * @date 2020-04-10
// * @url https://noahlan.com
// */
//@Configuration
//@ConditionalOnClass({
//        WebMvcAutoConfiguration.class,
//        PasswordEncoder.class,
//        TokenEnhancer.class,
//        Authentication.class})
//@EnableConfigurationProperties(SecurityProperties.class)
//@AllArgsConstructor
//public class SecurityAutoConfiguration {
//    private final SecurityProperties properties;
//
//    @Bean
//    @ConditionalOnExpression("!'${security.oauth2.client.ignore-urls}'.isEmpty()")
//    public PermitAllUrlResolver permitAllUrlResolver(WebApplicationContext webApplicationContext) {
//        return new PermitAllUrlResolver(webApplicationContext, properties);
//    }
//
//    @Bean
//    public ITISecurityInnerAspect securityInnerAspect() {
//        return new ITISecurityInnerAspect();
//    }
//
//    @Bean
//    @ConditionalOnMissingBean
//    public SecurityExceptionHandler securityExceptionHandler() {
//        return new SecurityExceptionHandler();
//    }
//
//    @Bean
//    public AcceptRequestInterceptor acceptRequestInterceptor() {
//        return new AcceptRequestInterceptor();
//    }
//
//    @Bean
//    @ConditionalOnMissingBean
//    public PasswordEncoder passwordEncoder() {
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//    }
//
//    @Bean
//    @ConditionalOnMissingBean
//    public UserDetailsBuilder userDetailsBuilder() {
//        return new DefaultUserDetailsBuilder();
//    }
//
//    @Bean
//    @Primary
//    public UserDetailsService itiUserDetailsService() {
//        return new ITIUserDetailsServiceImpl();
//    }
//
//    @Bean
//    @ConditionalOnMissingBean
//    public TokenEnhancer itiTokenEnhancer() {
//        return new DefaultTokenEnhancer();
//    }
//}
