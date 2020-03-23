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

package org.lan.iti.common.security.social;

import lombok.AllArgsConstructor;
import org.lan.iti.common.security.service.ITIUserDetailsService;
import org.lan.iti.common.security.social.connect.Connection;
import org.lan.iti.common.security.social.connect.UsersConnectionService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;

/**
 * Provider
 *
 * @author NorthLan
 * @date 2020-03-19
 * @url https://noahlan.com
 */
@AllArgsConstructor
public class SocialAuthenticationProvider implements AuthenticationProvider {
    private UsersConnectionService usersConnectionService;
    private ITIUserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(SocialAuthenticationToken.class, authentication, "unsupported authentication type");
        Assert.isTrue(!authentication.isAuthenticated(), "already authenticated");
        //
        SocialAuthenticationToken authToken = (SocialAuthenticationToken) authentication;
        String providerId = authToken.getProviderId();
        Connection<?> connection = authToken.getConnection();

        String userId = toUserId(connection);
        if (userId == null) {
            throw new BadCredentialsException("Unknown access token");
        }
        UserDetails userDetails = userDetailsService.loadUser(userId, connection.getKey().getProviderId(), null, null);
        if (userDetails == null) {
            throw new UsernameNotFoundException("Unknown connected account id");
        }
        return new SocialAuthenticationToken(connection, userDetails, authToken.getProviderAccountData(), getAuthorities(providerId, userDetails));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SocialAuthenticationToken.class.isAssignableFrom(authentication);
    }

    protected Collection<? extends GrantedAuthority> getAuthorities(String providerId, UserDetails userDetails) {
        return userDetails.getAuthorities();
    }

    protected String toUserId(Connection<?> connection) {
        List<String> userIds = usersConnectionService.findUserIdsWithConnection(connection);
        return (userIds.size() == 1) ? userIds.iterator().next() : null;
    }
}
