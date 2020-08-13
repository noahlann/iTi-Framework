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

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.security.service.ITIUserDetailsService;
import org.lan.iti.common.security.social.service.SocialAuthenticationService;
import org.lan.iti.common.security.social.service.SocialAuthenticationServiceConfigurer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.util.Assert;

/**
 * 社交鉴权提供者
 *
 * @author NorthLan
 * @date 2020-05-25
 * @url https://noahlan.com
 */
@Slf4j
public class SocialAuthenticationProvider implements AuthenticationProvider, InitializingBean {
    private final MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    // UserDetails
    @Setter
    private ITIUserDetailsService userDetailsService;
    @Setter
    private SocialAuthenticationServiceConfigurer registry;

    // checks
    private final UserDetailsChecker authenticationChecks = new DefaultAuthenticationChecks();

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(this.messages, "A message source must be set");
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(this.registry, "A service registry must be set");
        Assert.notNull(this.userDetailsService, "A user details service must be set");

        Assert.isInstanceOf(SocialAuthenticationToken.class, authentication, () -> messages.getMessage(
                "SocialAuthenticationProvider.onlySupports",
                "Only SocialAuthenticationToken is supported"));
        Assert.isTrue(!authentication.isAuthenticated(), "already authenticated.");

        SocialAuthenticationToken token = (SocialAuthenticationToken) authentication;

        // 处理token
        SocialAuthenticationService authenticationService = registry.getAuthenticationService(token.getProviderId());
        if (authenticationService == null) {
            throw new InvalidGrantException("系统未提供此登录方式");
        }
        authenticationService.processAuthToken(token);

        UserDetails user = authenticationService.retrieveUser(token, userDetailsService);
        if (user == null) {
            user = userDetailsService.loadUser(token.getPrincipal().toString(), token.getProviderId(), token.getDomain());
        }

        Assert.notNull(user,
                "retrieveUser returned null - a violation of the interface contract");

        authenticationChecks.check(user);
        authenticationService.additionalAuthenticationChecks(user, token);

        return createSuccessAuthentication(token, user);
    }

    protected Authentication createSuccessAuthentication(SocialAuthenticationToken token, UserDetails user) {
        SocialAuthenticationToken result = new SocialAuthenticationToken(token.getProviderId(),
                token.getDomain(),
                user,
                token.getCredentials(),
                user.getAuthorities(),
                token.getExtra());
        result.setDetails(token.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SocialAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private class DefaultAuthenticationChecks implements UserDetailsChecker {
        public void check(UserDetails user) {
            if (!user.isAccountNonLocked()) {
                log.debug("User account is locked");

                throw new LockedException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.locked",
                        "User account is locked"));
            }

            if (!user.isEnabled()) {
                log.debug("User account is disabled");

                throw new DisabledException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.disabled",
                        "User is disabled"));
            }

            if (!user.isAccountNonExpired()) {
                log.debug("User account is expired");

                throw new AccountExpiredException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.expired",
                        "User account has expired"));
            }

            if (!user.isCredentialsNonExpired()) {
                log.debug("User account credentials have expired");

                throw new CredentialsExpiredException(messages.getMessage(
                        "AbstractUserDetailsAuthenticationProvider.credentialsExpired",
                        "User credentials have expired"));
            }
        }
    }
}
