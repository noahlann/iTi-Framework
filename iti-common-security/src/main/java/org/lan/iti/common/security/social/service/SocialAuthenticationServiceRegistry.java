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

import java.util.*;

/**
 * SocialAuthenticationService 注册表
 *
 * @author NorthLan
 * @date 2020-05-25
 * @url https://noahlan.com
 */
public class SocialAuthenticationServiceRegistry {
    private Map<String, SocialAuthenticationService> authenticationServiceMap = new HashMap<>();

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

    /**
     * 添加一个service
     */
    public void addAuthenticationService(SocialAuthenticationService service) {
        this.authenticationServiceMap.put(service.getProviderId(), service);
    }

    /**
     * 添加多个service
     */
    public void addAuthenticationServices(SocialAuthenticationService... services) {
        for (SocialAuthenticationService service : services) {
            addAuthenticationService(service);
        }
    }

    /**
     * 添加一组services
     */
    public void setAuthenticationService(Collection<SocialAuthenticationService> authenticationServices) {
        for (SocialAuthenticationService authenticationService : authenticationServices) {
            addAuthenticationService(authenticationService);
        }
    }
}
