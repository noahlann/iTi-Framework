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

package org.lan.iti.cloud.security.handler;

import cn.hutool.core.util.StrUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Sso退出回调跳转
 *
 * @author NorthLan
 * @date 2021-03-11
 * @url https://noahlan.com
 */
public class SsoLogoutSuccessHandler implements LogoutSuccessHandler {
    private static final String REDIRECT_URL_KEY = "redirect_url";

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 获取请求中是否包含回调地址
        String redirectUrl = request.getParameter(REDIRECT_URL_KEY);
        String referer = request.getHeader(HttpHeaders.REFERER);

        if (StrUtil.isNotBlank(redirectUrl)) {
            response.sendRedirect(redirectUrl);
        } else if (StrUtil.isNotBlank(referer)) {
            // 默认跳转referer
            response.sendRedirect(referer);
        }
    }
}
