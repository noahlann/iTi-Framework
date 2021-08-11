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
//import feign.RequestInterceptor;
//import org.lan.iti.cloud.security.interceptor.ITIFeignClientInterceptor;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.cloud.commons.security.AccessTokenContextRelay;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.oauth2.client.OAuth2ClientContext;
//import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
//
///**
// * Feign 安全自动配置
// *
// * @author NorthLan
// * @date 2020-04-09
// * @url https://noahlan.com
// */
//@Configuration
//@ConditionalOnClass({
//        RequestInterceptor.class,
//        OAuth2ClientContext.class,
//        OAuth2ProtectedResourceDetails.class,
//        AccessTokenContextRelay.class
//})
//public class SecurityFeignAutoConfiguration {
//
//    @Bean
//    @ConditionalOnBean(AccessTokenContextRelay.class)
//    @ConditionalOnProperty("security.oauth2.client.client-id")
//    public RequestInterceptor oauth2FeignRequestInterceptor(OAuth2ClientContext oAuth2ClientContext,
//                                                            OAuth2ProtectedResourceDetails resource,
//                                                            AccessTokenContextRelay accessTokenContextRelay) {
//        return new ITIFeignClientInterceptor(oAuth2ClientContext, resource, accessTokenContextRelay);
//    }
//}
