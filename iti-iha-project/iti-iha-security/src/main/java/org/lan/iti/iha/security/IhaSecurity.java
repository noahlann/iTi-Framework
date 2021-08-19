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

package org.lan.iti.iha.security;

import lombok.experimental.UtilityClass;
import org.lan.iti.common.extension.ExtensionLoader;
import org.lan.iti.iha.security.authentication.Authentication;
import org.lan.iti.iha.security.clientdetails.ClientDetailsService;
import org.lan.iti.iha.security.config.SecurityConfig;
import org.lan.iti.iha.security.context.IhaSecurityContext;
import org.lan.iti.iha.security.context.SecurityContextHolder;
import org.lan.iti.iha.security.exception.SecurityException;
import org.lan.iti.iha.security.mgt.SecurityManager;
import org.lan.iti.iha.security.userdetails.UserDetails;
import org.lan.iti.iha.security.userdetails.UserDetailsService;

import javax.servlet.http.HttpServletRequest;

/**
 * IhaSecurity
 *
 * @author NorthLan
 * @date 2021/8/4
 * @url https://blog.noahlan.com
 */
@UtilityClass
public class IhaSecurity {
    private IhaSecurityContext context;

    public IhaSecurityContext getContext() {
        return context;
    }

    public void init(IhaSecurityContext context) {
        if (context == null) {
            throw new SecurityException("Unregistered context. Please use `IhaSecurity.registerContext(context)` to register context.");
        }
        IhaSecurity.context = context;

        load();
    }

    private void load() {
        if (context.getUserDetailsService() == null) {
            context.setUserDetailsService(ExtensionLoader.getLoader(UserDetailsService.class).getFirst());
        }
        if (context.getClientDetailsService() == null) {
            context.setClientDetailsService(ExtensionLoader.getLoader(ClientDetailsService.class).getFirst());
        }
    }

    public SecurityConfig getConfig() {
        return getContext().getConfig();
    }

    public SecurityManager getSecurityManager() {
        return getContext().getSecurityManager();
    }

    public boolean isAuthenticated(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return true;
        }
        return getUser(request) != null;
    }

    public UserDetails getUser(HttpServletRequest request) {
        UserDetails result = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                result = (UserDetails) authentication.getPrincipal();
            }
        }
        // TODO 完善getUser，想迁移到SecurityManager中，通过Provider获取?
//        if (result == null && request != null) {
//            result = getContext().getUserStoreService().get(request);
//            if (result != null) {
//                SecurityContextHolder.getContext().setAuthentication(new CachingAuthenticationToken(request, result.getAuthorities()));
//            }
//        }
        return result;
    }
}
