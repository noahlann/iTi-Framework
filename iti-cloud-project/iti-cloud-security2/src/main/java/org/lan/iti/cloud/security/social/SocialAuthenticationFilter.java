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

package org.lan.iti.cloud.security.social;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import lombok.Getter;
import lombok.Setter;
import org.lan.iti.cloud.security.social.service.SocialAuthenticationService;
import org.lan.iti.cloud.security.social.service.SocialAuthenticationServiceRegistry;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 统一 社交登录过滤器
 * <p>
 * <p>仅通过参数区分具体登录的方式,例如</p>
 * <p>手机号+密码+验证码登录: /social?providerId=mobile&principal=18888888888&credentials=123456&code=2333</p>
 * <p>微信登录+验证码登录:   /social?providerId=wechat&principal={wechat_code}</p>
 * <p>{@link org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter}</p>
 *
 * @author NorthLan
 * @date 2021-03-17
 * @url https://noahlan.com
 */
public class SocialAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final String SPRING_SECURITY_FORM_SOCIAL_KEY = "/social/login";

    @Getter
    @Setter
    private boolean postOnly = true;

    @Getter
    @Setter
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Setter
    private SocialAuthenticationServiceRegistry registry;

    public SocialAuthenticationFilter() {
        this(SPRING_SECURITY_FORM_SOCIAL_KEY);
    }

    public SocialAuthenticationFilter(String filterProcessesUrl) {
        super(new AntPathRequestMatcher(filterProcessesUrl, HttpMethod.POST.name()));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (postOnly && !request.getMethod().equals(HttpMethod.POST.name())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        // 数组类型将使用COMMA分割value
        Map<String, String> parameterMap = ServletUtil.getParamMap(request);
        // providerId
        String providerId = parameterMap.get(SocialConstants.PROVIDE_ID_KEY);
        if (StrUtil.isBlank(providerId)) {
            throw new ProviderNotFoundException("Provider not supported: " + providerId);
        }
        SocialAuthenticationService authenticationService = registry.getAuthenticationService(providerId);

        // 预登录 token
        SocialAuthenticationToken authenticationToken = authenticationService.authRequest(
                parameterMap.get("domain"),
                request,
                parameterMap);

        // 构建初始 details
        authenticationToken.setDetails(authenticationDetailsSource.buildDetails(request));

        Authentication authResult = null;
        try {
            authResult = this.getAuthenticationManager().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authResult);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();

            // TODO 这里的处理有点问题，统一输出了UsernameNotFoundException 无法自定义信息了
            try {
                authenticationEntryPoint.commence(request, response,
                        new UsernameNotFoundException(e.getLocalizedMessage(), e));
            } catch (Exception ignore) {
            }
        }
        return authResult;
    }
}
