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

package org.lan.iti.cloud.security.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.lan.iti.cloud.security.component.pkce.InMemoryPkceAuthorizationCodeServices;
import org.lan.iti.cloud.security.component.pkce.PkceAuthorizationCodeTokenGranter;
import org.lan.iti.cloud.security.component.pkce.RedisPkceAuthorizationCodeServices;
import org.lan.iti.cloud.security.feign.RemoteClientDetailsService;
import org.lan.iti.cloud.security.properties.SecurityProperties;
import org.lan.iti.cloud.security.service.ITIClientDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.util.ArrayList;
import java.util.List;

/**
 * iTi 授权服务器配置适配器
 *
 * @author NorthLan
 * @date 2020-05-23
 * @url https://noahlan.com
 */
@Configuration
@RequiredArgsConstructor
public class ITIAuthorizationServerConfigurerAdapter extends AuthorizationServerConfigurerAdapter {
    private final RemoteClientDetailsService remoteClientDetailsService;
    private final UserDetailsService itiUserDetailsService;

    private final AuthenticationManager authenticationManagerBean;
    private final ObjectMapper objectMapper;
    private final RedisConnectionFactory redisConnectionFactory;
    private final TokenEnhancer itiTokenEnhancer;
    private final TokenStore tokenStore;
    private final SecurityProperties properties;

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(itiClientDetailsService());
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.allowFormAuthenticationForClients()
                .authenticationEntryPoint(new ITICommenceAuthExceptionEntryPoint(objectMapper))
                .checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        // tokenGranter调用顺序必须位于authorizationCodeServices后
        AuthorizationCodeServices authorizationCodeServices;
        if (SecurityProperties.AUTHORIZATION_CODE_REDIS.equals(properties.getAuthorizationCodeStore())) {
            authorizationCodeServices = new RedisPkceAuthorizationCodeServices(itiClientDetailsService(), redisConnectionFactory);
        } else {
            authorizationCodeServices = new InMemoryPkceAuthorizationCodeServices(itiClientDetailsService());
        }
        endpoints
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
                .userDetailsService(itiUserDetailsService)
                .tokenStore(tokenStore)
                .tokenEnhancer(itiTokenEnhancer)
                .authenticationManager(authenticationManagerBean)
                .reuseRefreshTokens(false)
                .authorizationCodeServices(authorizationCodeServices)
                .tokenGranter(tokenGranter(endpoints))
                .exceptionTranslator(new ITIWebResponseExceptionTranslator());
    }

    @Bean
    @Primary
    public ClientDetailsService itiClientDetailsService() {
        return new ITIClientDetailsServiceImpl(remoteClientDetailsService);
    }

    /**
     * 仿制 Spring Security OAuth2 的默认tokenGranter设定，扩展PKCE的TokenGranter机制
     * {@link AuthorizationServerEndpointsConfigurer#getTokenGranter()} getDefaultTokenGranter()
     *
     * @param endpoints endpoints
     * @return 自定义的 TokenGranter
     */
    private TokenGranter tokenGranter(AuthorizationServerEndpointsConfigurer endpoints) {
        AuthorizationServerTokenServices tokenServices = endpoints.getTokenServices();
        AuthorizationCodeServices authorizationCodeServices = endpoints.getAuthorizationCodeServices();
        ClientDetailsService clientDetails = endpoints.getClientDetailsService();
        OAuth2RequestFactory requestFactory = endpoints.getOAuth2RequestFactory();

        List<TokenGranter> granters = new ArrayList<>();
        // origin granters
        granters.add(new RefreshTokenGranter(tokenServices, clientDetails, requestFactory));
        granters.add(new ImplicitTokenGranter(tokenServices, clientDetails, requestFactory));
        granters.add(new ClientCredentialsTokenGranter(tokenServices, clientDetails, requestFactory));
        granters.add(new ResourceOwnerPasswordTokenGranter(authenticationManagerBean, tokenServices,
                clientDetails, requestFactory));
        // PKCE granters conflict with original AuthorizationCodeTokenGranter
        // PkceAuthorizationCodeTokenGranter provider the default "Authorization Code Flow" and "Authorization Code with PKCE Flow"
        granters.add(new PkceAuthorizationCodeTokenGranter(tokenServices, authorizationCodeServices, clientDetails, requestFactory));

        return new TokenGranter() {
            private CompositeTokenGranter delegate;

            @Override
            public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
                if (delegate == null) {
                    delegate = new CompositeTokenGranter(granters);
                }
                return delegate.grant(grantType, tokenRequest);
            }
        };
    }
}
