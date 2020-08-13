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

package org.lan.iti.common.security.social.service;

import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.util.ProxyCreator;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SocialAuthenticationService 注册表
 *
 * @author NorthLan
 * @date 2020-05-25
 * @url https://noahlan.com
 */
@Setter
public class SocialAuthenticationServiceConfigurer {
    private final Map<String, SocialAuthenticationService> authenticationServiceMap = new HashMap<>();

    private AuthorizationServerTokenServices tokenServices;
    private ClientDetailsService clientDetailsService;
    private OAuth2RequestFactory oAuth2RequestFactory;

    private AuthenticationManager authenticationManager;
    private ApplicationContext applicationContext;

    /**
     * 添加多个service
     */
    public SocialAuthenticationServiceConfigurer authenticationServices(SocialAuthenticationService... services) {
        for (SocialAuthenticationService service : services) {
            this.authenticationServiceMap.put(service.getProviderId(), service);
        }
        return this;
    }

    public SocialAuthenticationService getAuthenticationService(String providerId) {
        SocialAuthenticationService authenticationService = authenticationServiceMap.get(providerId);
        if (authenticationService == null) {
            throw new IllegalArgumentException("No authentication service for service provider '" + providerId + "' is registered");
        }
        return authenticationService;
    }

    public List<SocialAuthenticationService> getAllAuthenticationServices() {
        return new ArrayList<>(authenticationServiceMap.values());
    }

    public ClientDetailsService getClientDetailsService() {
        return ProxyCreator.getProxy(ClientDetailsService.class, this::clientDetailsService);
    }

    @NonNull
    private ClientDetailsService clientDetailsService() {
        if (clientDetailsService == null) {
            this.clientDetailsService = applicationContext.getBean(ClientDetailsService.class);
        }
        return this.clientDetailsService;
    }


    public AuthorizationServerTokenServices getAuthorizationServerTokenServices() {
        return ProxyCreator.getProxy(AuthorizationServerTokenServices.class, this::authorizationServerTokenServices);
    }

    @NonNull
    private AuthorizationServerTokenServices authorizationServerTokenServices() {
        if (this.tokenServices == null) {
            this.tokenServices = applicationContext.getBean(AuthorizationServerTokenServices.class);
        }
        return this.tokenServices;
    }

    public OAuth2RequestFactory getOAuth2RequestFactory() {
        return ProxyCreator.getProxy(OAuth2RequestFactory.class, this::oAuth2RequestFactory);
    }

    @NonNull
    private OAuth2RequestFactory oAuth2RequestFactory() {
        if (this.oAuth2RequestFactory == null) {
            this.oAuth2RequestFactory = new DefaultOAuth2RequestFactory(getClientDetailsService());
        }
        return this.oAuth2RequestFactory;
    }

    public AuthenticationManager getAuthenticationManager() {
        return ProxyCreator.getProxy(AuthenticationManager.class, this::authenticationManager);
    }

    @NonNull
    private AuthenticationManager authenticationManager() {
        if (this.authenticationManager == null) {
            this.authenticationManager = applicationContext.getBean(AuthenticationManager.class);
        }
        return this.authenticationManager;
    }
}
