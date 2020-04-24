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

package org.lan.iti.common.security.component;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.oauth2.OAuth2AutoConfiguration;
import org.springframework.cloud.security.oauth2.client.AccessTokenContextRelay;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfiguration;

/**
 * 注入TokenRelay
 *
 * @author NorthLan
 * @date 2020-04-15
 * @url https://noahlan.com
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(OAuth2AutoConfiguration.class)
@ConditionalOnClass(ResourceServerConfiguration.class)
@ConditionalOnWebApplication
@ConditionalOnProperty("security.oauth2.client.client-id")
public class ITIResourceServerTokenRelayAutoConfiguration {
    @Bean
    public AccessTokenContextRelay accessTokenContextRelay(OAuth2ClientContext context) {
        return new AccessTokenContextRelay(context);
    }
}
