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

package org.lan.iti.cloud.security.social;

import lombok.RequiredArgsConstructor;
import org.lan.iti.cloud.security.feign.RemoteSocialService;
import org.lan.iti.cloud.security.social.config.ITISocialConfigurer;
import org.lan.iti.cloud.security.social.provider.mobile.MobileCodeAuthenticationService;
import org.lan.iti.cloud.security.social.provider.mobile.MobilePasswordAuthenticationService;
import org.lan.iti.cloud.security.social.provider.wechat.WechatAuthenticationService;
import org.lan.iti.cloud.security.social.service.SocialAuthenticationServiceRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 默认支持的社交登录配置
 *
 * @author NorthLan
 * @date 2021-03-18
 * @url https://noahlan.com
 */
@Configuration
@RequiredArgsConstructor
public class DefaultSocialConfigurer implements ITISocialConfigurer {
    private final RemoteSocialService remoteSocialService;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void configure(SocialAuthenticationServiceRegistry registry) {
        registry.authenticationServices(
                new WechatAuthenticationService(remoteSocialService),
                new MobilePasswordAuthenticationService(redisTemplate),
                new MobileCodeAuthenticationService(redisTemplate)
        );
    }
}
