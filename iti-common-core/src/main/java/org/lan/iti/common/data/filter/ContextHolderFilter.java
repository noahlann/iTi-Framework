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

package org.lan.iti.common.data.filter;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.constants.ITIConstants;
import org.lan.iti.common.data.dynamic.DomainContextHolder;
import org.lan.iti.common.data.tenant.TenantContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 属性持有过滤器
 *
 * @author NorthLan
 * @date 2020-02-24
 * @url https://noahlan.com
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class ContextHolderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, @NonNull HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String tenantId = httpServletRequest.getHeader(ITIConstants.TENANT_ID_HEADER_NAME);
        String domain = httpServletRequest.getHeader(ITIConstants.DOMAIN_HEADER_NAME);

        // tenantId
        if (StrUtil.isNotBlank(tenantId)) {
            TenantContextHolder.setTenantId(tenantId);
        }

        // domain
        if (StrUtil.isNotBlank(domain)) {
            DomainContextHolder.setDomain(domain);
        }

        log.debug("请求域 租户ID:[{}->{}] | 域domain:[{}->{}]",
                tenantId,
                TenantContextHolder.getTenantId(),
                domain,
                DomainContextHolder.getDomain());

        filterChain.doFilter(httpServletRequest, httpServletResponse);
        // 请求结束后清理内存
        TenantContextHolder.clear();
        DomainContextHolder.clear();
    }
}
