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

import org.lan.iti.common.core.util.SpringContextHolder;
import org.lan.iti.common.security.social.service.SocialAuthenticationService;
import org.lan.iti.common.security.social.service.SocialAuthenticationServiceRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Social 配置
 * <pre>
 *     增强oAuth2流程，扩展grant_type，便于进行社交登录
 * </pre>
 *
 * @author NorthLan
 * @date 2020-05-23
 * @url https://noahlan.com
 */
@Configuration
@Order(100 + 11)
public class SocialAuthorizationConfiguration extends AuthorizationServerConfigurerAdapter {

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenGranter(tokenGranter(endpoints.getTokenGranter()));
    }

    private TokenGranter tokenGranter(TokenGranter originTokenGranter) {
        return new TokenGranter() {
            private CompositeTokenGranter delegate;

            @Override
            public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
                if (delegate == null) {
                    List<TokenGranter> granters = new ArrayList<>();
                    granters.add(originTokenGranter);
                    SocialAuthenticationServiceRegistry registry = SpringContextHolder.getBean(SocialAuthenticationServiceRegistry.class);
                    for (SocialAuthenticationService it : registry.getAllAuthenticationServices()) {
                        if (it instanceof TokenGranter) {
                            granters.add((TokenGranter) it);
                        }
                    }
                    // AbstractSocialTokenGranter -> AbstractTokenGranter -> SocialAuthenticationService & TokenGranter
                    delegate = new CompositeTokenGranter(granters);
                }
                return delegate.grant(grantType, tokenRequest);
            }
        };
    }
}
