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

package org.lan.iti.cloud.security.web;

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.util.WebUtils;
import org.lan.iti.iha.security.context.SecurityContextHolder;
import org.lan.iti.iha.security.web.SecurityFilterChain;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author NorthLan
 * @date 2021/8/19
 * @url https://blog.noahlan.com
 */
@Slf4j
public class FilterChainProxy extends GenericFilterBean {
    private static final String FILTER_APPLIED = FilterChainProxy.class.getName().concat(".APPLIED");
    private final List<SecurityFilterChain> filterChains;

    public FilterChainProxy(SecurityFilterChain chain) {
        this(Collections.singletonList(chain));
    }

    public FilterChainProxy(List<SecurityFilterChain> filterChains) {
        this.filterChains = filterChains;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        boolean clearContext = request.getAttribute(FILTER_APPLIED) == null;
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        if (!clearContext) {
            doFilterInternal(httpServletRequest, httpServletResponse, chain);
            return;
        }
        try {
            request.setAttribute(FILTER_APPLIED, Boolean.TRUE);
            doFilterInternal(httpServletRequest, httpServletResponse, chain);
        } finally {
            SecurityContextHolder.clearContext();
            request.removeAttribute(FILTER_APPLIED);
        }
    }

    private void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        List<Filter> filters = getFilters(request);
        if (filters == null || filters.size() == 0) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("No security for %s", requestLine(request)));
            }
            chain.doFilter(request, response);
            return;
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Securing %s", requestLine(request)));
        }
        VirtualFilterChain virtualFilterChain = new VirtualFilterChain(request, chain, filters);
        virtualFilterChain.doFilter(request, response);
    }

    /**
     * Returns the first filter chain matching the supplied URL.
     *
     * @param request the request to match
     * @return an ordered array of Filters defining the filter chain
     */
    private List<Filter> getFilters(HttpServletRequest request) {
        int count = 0;
        for (SecurityFilterChain chain : this.filterChains) {
            if (log.isTraceEnabled()) {
                log.trace(String.format("Trying to match request against %s (%d/%d)", chain, ++count,
                        this.filterChains.size()));
            }
            if (chain.matches(request)) {
                return chain.getFilters();
            }
        }
        return null;
    }

    private static String requestLine(HttpServletRequest request) {
        return request.getMethod() + " " + WebUtils.buildRequestUrl(request);
    }

    public List<SecurityFilterChain> getFilterChains() {
        return Collections.unmodifiableList(this.filterChains);
    }

    /**
     * Internal {@code FilterChain} implementation that is used to pass a request through
     * the additional internal list of filters which match the request.
     */
    private static final class VirtualFilterChain implements FilterChain {

        private final FilterChain originalChain;

        private final List<Filter> additionalFilters;

        private final HttpServletRequest request;

        private final int size;

        private int currentPosition = 0;

        private VirtualFilterChain(HttpServletRequest request, FilterChain chain,
                                   List<Filter> additionalFilters) {
            this.originalChain = chain;
            this.additionalFilters = additionalFilters;
            this.size = additionalFilters.size();
            this.request = request;
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            if (this.currentPosition == this.size) {
                if (log.isDebugEnabled()) {
                    log.debug(String.format("Secured %s", requestLine(this.request)));
                }
                this.originalChain.doFilter(request, response);
                return;
            }
            this.currentPosition++;
            Filter nextFilter = this.additionalFilters.get(this.currentPosition - 1);
            if (log.isTraceEnabled()) {
                log.trace(String.format("Invoking %s (%d/%d)", nextFilter.getClass().getSimpleName(),
                        this.currentPosition, this.size));
            }
            nextFilter.doFilter(request, response, this);
        }

    }
}
