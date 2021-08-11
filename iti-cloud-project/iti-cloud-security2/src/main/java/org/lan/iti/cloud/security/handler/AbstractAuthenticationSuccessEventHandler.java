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

package org.lan.iti.cloud.security.handler;

import cn.hutool.core.collection.CollUtil;
import org.lan.iti.cloud.security.model.ITIUserDetails;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证成功处理器
 *
 * @author NorthLan
 * @date 2020-03-11
 * @url https://noahlan.com
 */
public abstract class AbstractAuthenticationSuccessEventHandler implements ApplicationListener<AuthenticationSuccessEvent> {

    @Override
    public void onApplicationEvent(@NonNull AuthenticationSuccessEvent event) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }
        HttpServletRequest request = requestAttributes.getRequest();
        HttpServletResponse response = requestAttributes.getResponse();
        Authentication authentication = (Authentication) event.getSource();
        if (isUserAuthentication(authentication)) {
            handle(authentication, request, response);
        }
    }

    private boolean isUserAuthentication(Authentication authentication) {
        return authentication.getPrincipal() instanceof ITIUserDetails
                || CollUtil.isNotEmpty(authentication.getAuthorities());
    }

    /**
     * 处理登录成功方法
     * <p>
     * 获取到登录的authentication 对象
     *
     * @param authentication 登录对象
     * @param request        请求
     * @param response       响应
     */
    public abstract void handle(Authentication authentication, HttpServletRequest request, HttpServletResponse response);
}
