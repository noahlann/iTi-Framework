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

package org.lan.iti.common.data.tenant;

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.constants.ITIConstants;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author NorthLan
 * @date 2020-02-24
 * @url https://noahlan.com
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class TenantContextHolderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String tenantIdStr = httpServletRequest.getHeader(ITIConstants.TENANT_ID_HEADER_NAME);

        log.debug("请求Header中租户ID为：{}", tenantIdStr);

        Long tenantId = ITIConstants.DEFAULT_TENANT_ID;
        try {
            tenantId = Long.parseLong(tenantIdStr);
        } catch (NumberFormatException e) {
            // just catch it
        }
        TenantContextHolder.setTenantId(tenantId);

        filterChain.doFilter(httpServletRequest, httpServletResponse);
        // 请求结束后清理内存
        TenantContextHolder.clear();
    }
}
