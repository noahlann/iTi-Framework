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

import feign.RequestInterceptor;
import org.lan.iti.common.security.interceptor.ITIFeignClientInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.security.oauth2.client.AccessTokenContextRelay;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;

/**
 * Feign 安全自动配置
 * TODO 其中过时的方法替换官方没有更新，待更新后重构
 *
 * @author NorthLan
 * @date 2020-04-09
 * @url https://noahlan.com
 */
@Configuration
@ConditionalOnClass({OAuth2ClientContext.class, OAuth2ProtectedResourceDetails.class, AccessTokenContextRelay.class})
@ConditionalOnProperty("security.oauth2.client.client-id")
public class SecurityFeignAutoConfiguration {

    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor(OAuth2ClientContext oAuth2ClientContext,
                                                            OAuth2ProtectedResourceDetails resource,
                                                            AccessTokenContextRelay accessTokenContextRelay) {
        return new ITIFeignClientInterceptor(oAuth2ClientContext, resource, accessTokenContextRelay);
    }
}
