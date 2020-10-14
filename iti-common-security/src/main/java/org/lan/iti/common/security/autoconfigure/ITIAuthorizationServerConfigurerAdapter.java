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

package org.lan.iti.common.security.autoconfigure;

import lombok.RequiredArgsConstructor;
import org.lan.iti.common.security.component.pkce.InMemoryPkceAuthorizationCodeServices;
import org.lan.iti.common.security.component.pkce.PkceAuthorizationCodeTokenGranter;
import org.lan.iti.common.security.properties.OAuth2ClientDetailsProperties;
import org.lan.iti.common.security.service.ITIClientDetailsServiceImpl;
import org.lan.iti.common.security.social.config.SocialSecurityConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
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

import javax.sql.DataSource;
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
@Order(1)
public class ITIAuthorizationServerConfigurerAdapter extends AuthorizationServerConfigurerAdapter {
    private final DataSource dataSource;
    private final OAuth2ClientDetailsProperties properties;

    private final ObjectProvider<SocialSecurityConfiguration> social;
    private final ObjectProvider<AuthenticationManager> authenticationManager;

    @Bean
    @Primary
    public ClientDetailsService itiClientDetailsService() {
        ITIClientDetailsServiceImpl itiClientDetailsService = new ITIClientDetailsServiceImpl(dataSource);

        itiClientDetailsService.setDeleteClientDetailsSql(properties.getDeleteClientDetailsSql());
        itiClientDetailsService.setInsertClientDetailsSql(properties.getInsertClientDetailsSql());
        itiClientDetailsService.setUpdateClientDetailsSql(properties.getUpdateClientDetailsSql());
        itiClientDetailsService.setUpdateClientSecretSql(properties.getUpdateClientSecretSql());
        itiClientDetailsService.setSelectClientDetailsSql(properties.getSelectClientDetailsSql());
        itiClientDetailsService.setFindClientDetailsSql(properties.getFindClientDetailsSql());
        return itiClientDetailsService;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.allowFormAuthenticationForClients()
                .checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(itiClientDetailsService());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        // tokenGranter调用顺序必须位于authorizationCodeServices后
        endpoints
                .authorizationCodeServices(new InMemoryPkceAuthorizationCodeServices(itiClientDetailsService()))
                .tokenGranter(tokenGranter(endpoints));
    }

    /**
     * 仿制 Spring Security OAuth2 的默认tokenGranter设定，自定义granter
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
//        granters.add(new AuthorizationCodeTokenGranter(tokenServices, authorizationCodeServices, clientDetails, requestFactory));
        granters.add(new RefreshTokenGranter(tokenServices, clientDetails, requestFactory));
        granters.add(new ImplicitTokenGranter(tokenServices, clientDetails, requestFactory));
        granters.add(new ClientCredentialsTokenGranter(tokenServices, clientDetails, requestFactory));
        authenticationManager.ifAvailable(it -> granters.add(new ResourceOwnerPasswordTokenGranter(it, tokenServices,
                clientDetails, requestFactory)));
        // PKCE granters conflict with original AuthorizationCodeTokenGranter
        // PkceAuthorizationCodeTokenGranter provider the default "Authorization Code Flow" and "Authorization Code with PKCE Flow"
        granters.add(new PkceAuthorizationCodeTokenGranter(tokenServices, authorizationCodeServices, clientDetails, requestFactory));

        // social granters AbstractSocialTokenGranter -> AbstractTokenGranter -> SocialAuthenticationService & TokenGranter
        social.ifAvailable(reg -> reg.getSocialConfigurer().getAllAuthenticationServices().forEach(it -> {
            if (it instanceof TokenGranter) {
                granters.add((TokenGranter) it);
            }
        }));

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
