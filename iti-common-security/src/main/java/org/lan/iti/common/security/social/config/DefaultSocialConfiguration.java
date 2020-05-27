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

import org.lan.iti.common.security.social.mobile.MobileCodeAuthenticationService;
import org.lan.iti.common.security.social.mobile.MobilePasswordAuthenticationService;
import org.lan.iti.common.security.social.service.SocialAuthenticationServiceRegistry;
import org.lan.iti.common.security.social.wechat.WechatAuthenticationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * 默认的社交配置，添加框架默认的几种登录方式
 * <pre>
 *     手机号登录
 *     邮箱登录
 *     微信登录
 * </pre>
 *
 * @author NorthLan
 * @date 2020-05-25
 * @url https://noahlan.com
 */
@Configuration
public class DefaultSocialConfiguration extends ITISocialAdapter {

    @Override
    public void addAuthenticationServices(SocialAuthenticationServiceRegistry registry) {
        registry.addAuthenticationServices(
                mobilePasswordAuthenticationService(),
                mobileCodeAuthenticationService(),
                wechatAuthenticationService());
    }

    @Bean
    @Lazy
    public MobilePasswordAuthenticationService mobilePasswordAuthenticationService() {
        return new MobilePasswordAuthenticationService(getAuthenticationManager(),
                getAuthorizationServerTokenServices(),
                getClientDetailsService(),
                getOAuth2RequestFactory());
    }

    @Bean
    @Lazy
    public MobileCodeAuthenticationService mobileCodeAuthenticationService() {
        return new MobileCodeAuthenticationService(getAuthenticationManager(),
                getAuthorizationServerTokenServices(),
                getClientDetailsService(),
                getOAuth2RequestFactory());
    }

    @Bean
    @Lazy
    public WechatAuthenticationService wechatAuthenticationService() {
        return new WechatAuthenticationService(getAuthenticationManager(),
                getAuthorizationServerTokenServices(),
                getClientDetailsService(),
                getOAuth2RequestFactory());
    }
}
