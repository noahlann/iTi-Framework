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

import org.lan.iti.iha.security.context.HttpSessionSecurityContextRepository;
import org.lan.iti.iha.security.context.SecurityContextFilterHelper;
import org.lan.iti.iha.security.context.SecurityContextRepository;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author NorthLan
 * @date 2021/8/19
 * @url https://blog.noahlan.com
 */
public class SecurityContextPersistenceFilter extends GenericFilterBean {
    private final SecurityContextFilterHelper contextFilterHelper;

    public SecurityContextPersistenceFilter() {
        this(new HttpSessionSecurityContextRepository());
    }

    public SecurityContextPersistenceFilter(SecurityContextRepository repository) {
        this.contextFilterHelper = new SecurityContextFilterHelper(repository);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        contextFilterHelper.doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    public void setForceEagerSessionCreation(boolean val) {
        contextFilterHelper.setForceEagerSessionCreation(val);
    }
}
