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

package org.lan.iti.cloud.tenant;

import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.constants.CommonConstants;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 租户信息注入
 * <p>
 * 数据来源<br/>
 * 1. 请求头 <br/>
 * 2. 请求参数 <br/>
 *
 * @author NorthLan
 * @date 2021-03-10
 * @url https://noahlan.com
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantContextHolderFilter extends GenericFilterBean {

    @Override
    @SneakyThrows
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String headerTenantId = request.getHeader(CommonConstants.TENANT_ID_HEADER_NAME);
        String paramTenantId = request.getParameter(CommonConstants.TENANT_ID_HEADER_NAME);

        log.trace("请求中的租户ID -> Header:[{}] Param: [{}]", headerTenantId, paramTenantId);

        if (StrUtil.isNotBlank(headerTenantId)) {
            TenantContextHolder.setTenantId(headerTenantId);
        } else if (StrUtil.isNotBlank(paramTenantId)) {
            TenantContextHolder.setTenantId(paramTenantId);
        } else {
            TenantContextHolder.setTenantId(CommonConstants.DEFAULT_TENANT_ID);
        }
        chain.doFilter(request, response);
        TenantContextHolder.clear();
    }
}
