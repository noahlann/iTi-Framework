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

package org.lan.iti.common.security.social;

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.util.Formatter;
import org.lan.iti.common.security.model.ITIUser;
import org.lan.iti.common.security.social.connect.UsersConnectionService;
import org.lan.iti.common.security.social.exception.SocialAuthenticationException;
import org.lan.iti.common.security.social.provider.SocialAuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

/**
 * 社交过滤器,添加进Spring Security过滤链中
 * 需在 PRE_AUTH_FILTER 之前加入此过滤器
 * <p>
 * 支持不同提供商登录,默认路径应当为 /social/providerId
 *
 * @author NorthLan
 * @date 2020-03-16
 * @url https://noahlan.com
 */
@Slf4j
public class SocialAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final String DEFAULT_FILTER_PROCESSES_URL = "/social";

    private SocialAuthenticationServiceLocator authServiceLocator;
    private UsersConnectionService usersConnectionService;

    private String filterProcessesUrl = DEFAULT_FILTER_PROCESSES_URL;

    protected SocialAuthenticationFilter(AuthenticationManager authManager,
                                         UsersConnectionService usersConnectionService,
                                         SocialAuthenticationServiceLocator socialAuthenticationServiceLocator) {
        super(DEFAULT_FILTER_PROCESSES_URL);
        setAuthenticationManager(authManager);
        this.authServiceLocator = socialAuthenticationServiceLocator;
        this.usersConnectionService = usersConnectionService;
        // TODO 一些依赖的东西
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {
        if (detectRejection(httpServletRequest)) {
            log.debug("登录请求被拒绝,登录失败!");
            throw new SocialAuthenticationException("Authentication failed because user rejected authorization.");
        }
        Authentication authentication;
        Set<String> providers = authServiceLocator.registeredAuthenticationProviderIds();
        String providerId = getRequestedProviderId(httpServletRequest);
        if (providerId != null && !providers.isEmpty() && providers.contains(providerId)) {
            // 服务提供商匹配
            SocialAuthenticationService<?> authService = authServiceLocator.getAuthenticationService(providerId);
            authentication = attemptAuthService(authService, httpServletRequest, httpServletResponse);
            if (authentication == null) {
                throw new AuthenticationServiceException("authentication failed");
            }
        } else {
            throw new SocialAuthenticationException(Formatter.format("服务提供商未能匹配. req: {}", providerId));
        }
        return authentication;
    }

    /**
     * 检测请求拒绝
     *
     * @param request 登录请求
     */
    protected boolean detectRejection(HttpServletRequest request) {
        Set<?> parameterKeys = request.getParameterMap().keySet();
        if ((parameterKeys.size() == 1) && (parameterKeys.contains("state"))) {
            return false;
        }
        return parameterKeys.size() > 0
                && !parameterKeys.contains("oauth_token")
                && !parameterKeys.contains("code")
                && !parameterKeys.contains("scope");
    }

    private Authentication attemptAuthService(final SocialAuthenticationService<?> authService,
                                              final HttpServletRequest request,
                                              final HttpServletResponse response) {
        final SocialAuthenticationToken token = authService.getAuthToken(request, response);
        if (token == null) {
            return null;
        }

        Assert.notNull(token.getConnection(), "connection must not be null.");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            // 未认证或未登录
            return doAuthentication(authService, request, token);
        } else {
            // 已登录 TODO addConnection
            return null;
        }
    }

    private Authentication doAuthentication(SocialAuthenticationService<?> authService,
                                            HttpServletRequest request,
                                            SocialAuthenticationToken token) {
        try {
            if (!authService.getConnectionCardinality().isAuthenticatePossible()) {
                return null;
            }
            token.setDetails(authenticationDetailsSource.buildDetails(request));
            Authentication success = getAuthenticationManager().authenticate(token);
            Assert.isInstanceOf(ITIUser.class, success.getPrincipal(), "unexpected principle type");
            // TODO updateConnection
            return success;
        } catch (BadCredentialsException e) {
            // TODO signUp
            throw e;
        }
    }

    @Override
    public void setFilterProcessesUrl(String filterProcessesUrl) {
        super.setFilterProcessesUrl(filterProcessesUrl);
        this.filterProcessesUrl = filterProcessesUrl;
    }


    /**
     * 获取ProviderId
     *
     * @param request 请求
     * @return 从请求url中获取到的ProviderId
     */
    private String getRequestedProviderId(HttpServletRequest request) {
        String uri = request.getRequestURI();
        int pathParamIndex = uri.indexOf(';');

        if (pathParamIndex > 0) {
            // strip everything after the first semi-colon
            uri = uri.substring(0, pathParamIndex);
        }

        // uri must start with context path
        uri = uri.substring(request.getContextPath().length());

        // remaining uri must start with filterProcessesUrl
        if (!uri.startsWith(filterProcessesUrl)) {
            return null;
        }
        uri = uri.substring(filterProcessesUrl.length());

        // expect /filterprocessesurl/provider, not /filterprocessesurlproviderr
        if (uri.startsWith("/")) {
            return uri.substring(1);
        } else {
            return null;
        }
    }
}
