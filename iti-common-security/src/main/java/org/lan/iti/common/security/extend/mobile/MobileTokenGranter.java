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

package org.lan.iti.common.security.extend.mobile;

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.security.extend.ITIAuthenticationToken;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 自定义grant_type类型
 * <p>
 * grant_type: mobile
 * params: mobile,code,credentials
 * </p>
 * <p>
 * 1. 手机号/密码    登录
 * 2. 手机号/验证码  登录
 * </p>
 *
 * @author NorthLan
 * @date 2020-03-12
 * @url https://noahlan.com
 */
@Slf4j
public class MobileTokenGranter extends AbstractTokenGranter {
    public static final String GRANT_TYPE = "mobile";
    // PARAMETER_KEY
    public static final String PARAMETER_KEY_MOBILE = "mobile";
    public static final String PARAMETER_KEY_CODE = "code";
    public static final String PARAMETER_KEY_PASSWORD = "password";

    // authenticationManager
    private final AuthenticationManager authenticationManager;

    protected MobileTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType, AuthenticationManager authenticationManager) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());
        String mobile = parameters.get(PARAMETER_KEY_MOBILE);
        String code = parameters.get(PARAMETER_KEY_CODE);
        String credentials = parameters.get(PARAMETER_KEY_PASSWORD);

        // Protect from downstream leaks
        parameters.remove(PARAMETER_KEY_PASSWORD);
        parameters.remove(PARAMETER_KEY_CODE);

        //
        Authentication authenticationToken = new ITIAuthenticationToken(null);
        ((AbstractAuthenticationToken) authenticationToken).setDetails(parameters);
        try {
            authenticationToken = authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {

        }

        // TODO get UserAuth

        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }
}
