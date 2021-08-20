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

package org.lan.iti.cloud.security.context;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.iha.security.authentication.Authentication;
import org.lan.iti.iha.security.authentication.support.AnonymousAuthenticationToken;
import org.lan.iti.iha.security.context.SecurityContextHolder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author NorthLan
 * @date 2021/8/19
 * @url https://blog.noahlan.com
 */
@Slf4j
@Getter
public class AnonymousAuthenticationFilter extends GenericFilterBean implements InitializingBean {
    private final Object principal;
    private final Collection<String> authorities;

    public AnonymousAuthenticationFilter() {
        this("anonymousUser", new HashSet<>());
    }

    public AnonymousAuthenticationFilter(Object principal, Collection<String> authorities) {
        Assert.notNull(principal, "Anonymous authentication principal must be set");
        Assert.notNull(authorities, "Anonymous authorities must be set");
        this.principal = principal;
        this.authorities = authorities;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.getContext().setAuthentication(createAuthentication((HttpServletRequest) request));
            if (this.logger.isTraceEnabled()) {
                log.trace(String.format("Set SecurityContextHolder to %s", SecurityContextHolder.getContext().getAuthentication().toString()));
            } else {
                log.debug("Set SecurityContextHolder to anonymous SecurityContext");
            }
        } else {
            log.trace(String.format("Did not set SecurityContextHolder since already authenticated %s",
                    SecurityContextHolder.getContext().getAuthentication().toString()));
        }
        chain.doFilter(request, response);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(this.principal, "Anonymous authentication principal must be set");
        Assert.notNull(this.authorities, "Anonymous authorities must be set");
    }

    protected Authentication createAuthentication(HttpServletRequest request) {
        return new AnonymousAuthenticationToken();
    }
}
