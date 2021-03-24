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

import org.lan.iti.cloud.tenant.TenantContextHolder;
import org.lan.iti.common.core.constants.CommonConstants;
import org.lan.iti.common.core.util.Formatter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 增强成功回调增加租户上下文避免极端情况下丢失问题
 *
 * @author NorthLan
 * @date 2021-03-10
 * @url https://noahlan.com
 */
public class TenantSavedRequestAwareAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final RequestCache requestCache = new HttpSessionRequestCache();

    public TenantSavedRequestAwareAuthenticationSuccessHandler() {
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        SavedRequest savedRequest = this.requestCache.getRequest(request, response);
        if (savedRequest == null) {
            super.onAuthenticationSuccess(request, response, authentication);
        }
        if (isAlwaysUseDefaultTargetUrl()) {
            this.requestCache.removeRequest(request, response);
            super.onAuthenticationSuccess(request, response, authentication);
        } else {
            this.clearAuthenticationAttributes(request);
            assert savedRequest != null;
            String targetUrl = Formatter.format("{}&{}={}",
                    savedRequest.getRedirectUrl(),
                    CommonConstants.TENANT_ID_HEADER_NAME,
                    TenantContextHolder.getTenantId());
            this.getRedirectStrategy().sendRedirect(request, response, targetUrl);
        }
    }
}
