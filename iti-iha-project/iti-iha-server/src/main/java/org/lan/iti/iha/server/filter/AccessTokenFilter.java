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

package org.lan.iti.iha.server.filter;

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.iha.server.IhaServer;
import org.lan.iti.iha.server.pipeline.Pipeline;
import org.lan.iti.iha.server.util.TokenUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@Slf4j
public class AccessTokenFilter extends AbstractFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
        String ignoreUrl = filterConfig.getInitParameter("ignoreUrl");
        this.initIgnoreUrls(ignoreUrl);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Pipeline<Object> idsFilterErrorPipeline = IhaServer.getContext().getFilterPipeline();
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        boolean ignored = this.isIgnoredServletPath(request);
        if (ignored) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        log.debug("{} - {}", request.getMethod(), request.getRequestURI());
        String accessToken = TokenUtil.getAccessToken(request);
        try {
            TokenUtil.validateAccessToken(accessToken);
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            this.getFilterErrorPipeline(idsFilterErrorPipeline).errorHandle(servletRequest, servletResponse, e);
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
