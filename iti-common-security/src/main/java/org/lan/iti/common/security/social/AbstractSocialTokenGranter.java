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

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.constants.SecurityConstants;
import org.lan.iti.common.security.social.service.SocialAuthenticationService;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 扩展社交登录
 *
 * @author NorthLan
 * @date 2020-05-23
 * @url https://noahlan.com
 */
@Slf4j
public abstract class AbstractSocialTokenGranter extends AbstractTokenGranter implements SocialAuthenticationService {
    private final AuthenticationManager authenticationManager;
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    @Getter
    private String grantType;

    protected AbstractSocialTokenGranter(AuthenticationManager authenticationManager,
                                         AuthorizationServerTokenServices tokenServices,
                                         ClientDetailsService clientDetailsService,
                                         OAuth2RequestFactory requestFactory,
                                         String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.authenticationManager = authenticationManager;
        this.grantType = grantType;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
        String domain = parameters.get(SecurityConstants.DOMAIN);
        // check domain
        if (StrUtil.isBlank(domain)) {
            log.warn("domain must not be null or empty: [{}]. use [{}] instead.", domain, SecurityConstants.DEFAULT_DOMAIN);
            domain = SecurityConstants.DEFAULT_DOMAIN;
        }

        Authentication userAuth = getAuthToken(domain, tokenRequest, parameters);
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);
        try {
            // 获取用户信息
            userAuth = authenticationManager.authenticate(userAuth);
        } catch (AccountStatusException | BadCredentialsException e) {
            throw new InvalidGrantException(e.getMessage());
        }
        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw new InvalidGrantException("Could not authenticate");
        }

        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }
}
