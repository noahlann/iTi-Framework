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

package org.lan.iti.cloud.security.web.matcher;

import lombok.Getter;
import lombok.Setter;
import org.lan.iti.iha.security.matcher.RequestMatcher;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import org.springframework.web.servlet.handler.MatchableHandlerMapping;
import org.springframework.web.servlet.handler.RequestMatchResult;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author NorthLan
 * @date 2021/10/15
 * @url https://blog.noahlan.com
 */
public class MvcRequestMatcher implements RequestMatcher {
    private final DefaultMatcher defaultMatcher = new DefaultMatcher();
    private final HandlerMappingIntrospector introspector;
    private final String pattern;

    @Setter
    private HttpMethod method;

    @Setter
    @Getter
    private String servletPath;

    public MvcRequestMatcher(HandlerMappingIntrospector introspector, String pattern) {
        this.introspector = introspector;
        this.pattern = pattern;
    }
    
    @Override
    public boolean matches(HttpServletRequest request) {
        if (notMatchMethodOrServletPath(request)) {
            return false;
        }
        MatchableHandlerMapping mapping = getMapping(request);
        if (mapping == null) {
            return this.defaultMatcher.matches(request);
        }
        RequestMatchResult matchResult = mapping.match(request, this.pattern);
        return matchResult != null;
    }

    @Override
    public MatchResult matcher(HttpServletRequest request) {
        if (notMatchMethodOrServletPath(request)) {
            return MatchResult.notMatch();
        }
        MatchableHandlerMapping mapping = getMapping(request);
        if (mapping == null) {
            return this.defaultMatcher.matcher(request);
        }
        RequestMatchResult result = mapping.match(request, this.pattern);
        return (result != null) ? MatchResult.match(result.extractUriTemplateVariables()) : MatchResult.notMatch();
    }

    private boolean notMatchMethodOrServletPath(HttpServletRequest request) {
        return this.method != null && !this.method.name().equals(request.getMethod())
                || this.servletPath != null && !this.servletPath.equals(request.getServletPath());
    }

    private MatchableHandlerMapping getMapping(HttpServletRequest request) {
        try {
            return this.introspector.getMatchableHandlerMapping(request);
        }
        catch (Throwable ex) {
            return null;
        }
    }

    private class DefaultMatcher implements RequestMatcher {
        private final UrlPathHelper pathHelper = new UrlPathHelper();
        private final PathMatcher pathMatcher = new AntPathMatcher();

        @Override
        public boolean matches(HttpServletRequest request) {
            String lookupPath = this.pathHelper.getLookupPathForRequest(request);
            return matches(lookupPath);
        }

        private boolean matches(String lookupPath) {
            return this.pathMatcher.match(MvcRequestMatcher.this.pattern, lookupPath);
        }

        @Override
        public MatchResult matcher(HttpServletRequest request) {
            String lookupPath = this.pathHelper.getLookupPathForRequest(request);
            if (matches(lookupPath)) {
                Map<String, String> variables = this.pathMatcher
                        .extractUriTemplateVariables(MvcRequestMatcher.this.pattern, lookupPath);
                return MatchResult.match(variables);
            }
            return MatchResult.notMatch();
        }
    }
}
