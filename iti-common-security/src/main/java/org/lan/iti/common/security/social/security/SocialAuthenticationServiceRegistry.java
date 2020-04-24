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

package org.lan.iti.common.security.social.security;

import org.lan.iti.common.security.social.connect.support.ConnectionFactoryRegistry;
import org.lan.iti.common.security.social.security.provider.SocialAuthenticationService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 社交认证服务注册表
 *
 * @author NorthLan
 * @date 2020-03-18
 * @url https://noahlan.com
 */
public class SocialAuthenticationServiceRegistry extends ConnectionFactoryRegistry implements SocialAuthenticationServiceLocator {
    private Map<String, SocialAuthenticationService<?>> authenticationServices = new HashMap<>();

    @Override
    public SocialAuthenticationService<?> getAuthenticationService(String providerId) {
        SocialAuthenticationService<?> authenticationService = authenticationServices.get(providerId);
        if (authenticationService == null) {
            throw new IllegalArgumentException("No authentication service for service provider '" + providerId + "' is registered");
        }
        return authenticationService;
    }

    /**
     * 将{@link SocialAuthenticationService}添加到此注册表.
     */
    public void addAuthenticationService(SocialAuthenticationService<?> authenticationService) {
        addConnectionFactory(authenticationService.getConnectionFactory());
        authenticationServices.put(authenticationService.getConnectionFactory().getProviderId(), authenticationService);
    }

    /**
     * 将一组 {@link SocialAuthenticationService}添加到此注册表.
     */
    public void setAuthenticationServices(Iterable<SocialAuthenticationService<?>> authenticationServices) {
        for (SocialAuthenticationService<?> authenticationService : authenticationServices) {
            addAuthenticationService(authenticationService);
        }
    }

    @Override
    public Set<String> registeredAuthenticationProviderIds() {
        return authenticationServices.keySet();
    }
}
