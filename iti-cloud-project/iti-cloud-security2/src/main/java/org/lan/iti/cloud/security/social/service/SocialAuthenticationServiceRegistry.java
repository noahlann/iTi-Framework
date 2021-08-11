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

package org.lan.iti.cloud.security.social.service;

import lombok.Setter;

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
public class SocialAuthenticationServiceRegistry {
    private final Map<String, SocialAuthenticationService> AUTHENTICATION_SERVICE_MAP = new HashMap<>();

    /**
     * 添加多个service
     */
    public SocialAuthenticationServiceRegistry authenticationServices(SocialAuthenticationService... services) {
        for (SocialAuthenticationService service : services) {
            AUTHENTICATION_SERVICE_MAP.put(service.getProviderId(), service);
        }
        return this;
    }

    public SocialAuthenticationServiceRegistry authenticationService(SocialAuthenticationService service) {
        AUTHENTICATION_SERVICE_MAP.put(service.getProviderId(), service);
        return this;
    }

    public SocialAuthenticationService getAuthenticationService(String providerId) {
        SocialAuthenticationService authenticationService = AUTHENTICATION_SERVICE_MAP.get(providerId);
        if (authenticationService == null) {
            throw new IllegalArgumentException("No authentication service for service provider '" + providerId + "' is registered");
        }
        return authenticationService;
    }

    public List<SocialAuthenticationService> getAllAuthenticationServices() {
        return new ArrayList<>(AUTHENTICATION_SERVICE_MAP.values());
    }
}
