/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.common.security.extend;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.security.properties.SecurityProperties;
import org.lan.iti.common.security.service.ITIUserDetailsService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

/**
 * @author NorthLan
 * @date 2020-03-12
 * @url https://noahlan.com
 */
@Slf4j
@AllArgsConstructor
public class ITIAuthenticationProvider implements AuthenticationProvider {
    private final ITIUserDetailsService userDetailsService;
    private final SecurityProperties properties;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        ITIAuthenticationToken authenticationToken = (ITIAuthenticationToken) authentication;

        String principal = authenticationToken.getPrincipal().toString();
        String type = authenticationToken.getType();
        String credentials = authenticationToken.getCredentials().toString();
        Map<String, String> extraMap = authenticationToken.getExtraMap();

        UserDetails userDetails = userDetailsService.loadUser(principal, type, credentials, extraMap);

        // TODO UsernameNotFoundException

        // TODO check

        ITIAuthenticationToken newAuthenticationToken = new ITIAuthenticationToken(userDetails, credentials, type, userDetails.getAuthorities());
        newAuthenticationToken.setDetails(authenticationToken.getDetails());
        return newAuthenticationToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        if (authentication == null) {
            return false;
        }
        return ITIAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
