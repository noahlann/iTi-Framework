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

package org.lan.iti.iha.security.context;

import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * SecurityContextHolder 的过滤器帮助类
 * <p>
 * 普通环境下可直接用于Filter
 * Spring环境下用于GenericFilterBean
 *
 * @author NorthLan
 * @date 2021/8/19
 * @url https://blog.noahlan.com
 */
@Accessors(chain = true)
@Slf4j
public class SecurityContextFilterHelper {
    static final String FILTER_APPLIED = "__iha_security_scpf_applied";

    private final SecurityContextRepository repository;

    @Setter
    private boolean forceEagerSessionCreation = false;

    public SecurityContextFilterHelper(SecurityContextRepository repository) {
        this.repository = repository;
    }

    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // ensure that filter is only applied once per request
        if (request.getAttribute(FILTER_APPLIED) != null) {
            chain.doFilter(request, response);
            return;
        }
        request.setAttribute(FILTER_APPLIED, Boolean.TRUE);
        if (this.forceEagerSessionCreation) {
            HttpSession session = request.getSession();
            if (session.isNew()) {
                log.debug(String.format("Created session %s eagerly", session.getId()));
            }
        }
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContext contextBeforeChainExecution = this.repository.loadContext(holder);
        try {
            SecurityContextHolder.setContext(contextBeforeChainExecution);
            if (contextBeforeChainExecution.getAuthentication() == null) {
                log.debug("Set SecurityContextHolder to empty SecurityContext");
            } else {
                log.debug(String.format("Set SecurityContextHolder to %s", contextBeforeChainExecution));
            }
            chain.doFilter(holder.getRequest(), holder.getResponse());
        } finally {
            SecurityContext contextAfterChainExecution = SecurityContextHolder.getContext();
            // Crucial removal of SecurityContextHolder contents before anything else.
            SecurityContextHolder.clearContext();
            this.repository.saveContext(contextAfterChainExecution, holder.getRequest(), holder.getResponse());
            request.removeAttribute(FILTER_APPLIED);
            log.debug("Cleared SecurityContextHolder to complete request");
        }
    }
}
