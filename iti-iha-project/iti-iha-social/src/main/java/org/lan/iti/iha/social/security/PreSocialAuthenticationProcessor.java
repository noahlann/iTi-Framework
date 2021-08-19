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

package org.lan.iti.iha.social.security;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.request.AuthRequest;
import org.lan.iti.iha.security.authentication.Authentication;
import org.lan.iti.iha.security.exception.authentication.AuthenticationException;
import org.lan.iti.iha.security.mgt.RequestParameter;
import org.lan.iti.iha.security.processor.ProcessChain;
import org.lan.iti.iha.social.JustAuthRequestContext;
import org.lan.iti.iha.social.SocialConfig;

/**
 * @author NorthLan
 * @date 2021/8/14
 * @url https://blog.noahlan.com
 */
public class PreSocialAuthenticationProcessor extends AbstractSocialAuthenticationProcessor {
    private AuthStateCache authStateCache;

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 1000;
    }

    @Override
    public boolean support(RequestParameter parameter, Authentication authentication) throws AuthenticationException {
        return authentication == null && parameter instanceof SocialRequestParameter;
    }

    @Override
    public Authentication process(RequestParameter parameter, Authentication authentication, ProcessChain chain) throws AuthenticationException {
        SocialRequestParameter socialRequestParameter = (SocialRequestParameter) parameter;
        SocialConfig socialConfig = socialRequestParameter.getConfig();

        AuthRequest authRequest = getAuthRequest(socialConfig);
        AuthCallback authCallback = this.parseRequest(parameter);
        return chain.process(socialRequestParameter, new SocialAuthenticationToken(authRequest, authCallback));
    }

    private AuthRequest getAuthRequest(SocialConfig config) {
        String source = config.getPlatform();

        // Get the AuthConfig of JustAuth
        AuthConfig authConfig = config.getJustAuthConfig();
        if (ObjectUtil.isNull(authConfig)) {
            throw new SecurityException("Missing AuthConfig...");
        }
        // Instantiate the AuthRequest of JustAuth
        return JustAuthRequestContext.getRequest(source, config, authConfig, authStateCache);
    }

    /**
     * Parse the callback request and get the callback parameter object
     *
     * @param parameter Current callback request parameter
     * @return AuthCallback
     */
    private AuthCallback parseRequest(RequestParameter parameter) {
        if (MapUtil.isEmpty(parameter)) {
            return new AuthCallback();
        }
        AuthCallback result;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            result = objectMapper.convertValue(parameter, AuthCallback.class);
        } catch (IllegalArgumentException e) {
            result = new AuthCallback();
        }
        return result;
    }
}
