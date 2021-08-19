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

package org.lan.iti.iha.security.matcher;

import cn.hutool.core.lang.Assert;
import lombok.Getter;
import org.lan.iti.common.core.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

/**
 * AntPathRequestMatcher
 *
 * @author NorthLan
 * @date 2021/7/30
 * @url https://blog.noahlan.com
 */
public class AntPathRequestMatcher implements RequestMatcher {
    private static final String MATCH_ALL = "/**";

    private final Matcher matcher;

    @Getter
    private final String pattern;

    @Getter
    private final String httpMethod;

    @Getter
    private final boolean caseSensitive;

    /**
     * Creates a matcher with the specific pattern which will match all HTTP methods in a
     * case sensitive manner.
     *
     * @param pattern the ant pattern to use for matching
     */
    public AntPathRequestMatcher(String pattern) {
        this(pattern, null);
    }

    /**
     * Creates a matcher with the supplied pattern and HTTP method in a case sensitive
     * manner.
     *
     * @param pattern    the ant pattern to use for matching
     * @param httpMethod the HTTP method. The {@code matches} method will return false if
     *                   the incoming request doesn't have the same method.
     */
    public AntPathRequestMatcher(String pattern, String httpMethod) {
        this(pattern, httpMethod, true);
    }

    /**
     * Creates a matcher with the supplied pattern which will match the specified Http
     * method
     *
     * @param pattern       the ant pattern to use for matching
     * @param httpMethod    the HTTP method. The {@code matches} method will return false if
     *                      the incoming request doesn't doesn't have the same method.
     * @param caseSensitive true if the matcher should consider case, else false
     */
    public AntPathRequestMatcher(String pattern, String httpMethod, boolean caseSensitive) {
        Assert.notEmpty(pattern, "Pattern cannot be null or empty");
        this.caseSensitive = caseSensitive;
        if (pattern.equals(MATCH_ALL) || pattern.equals("**")) {
            pattern = MATCH_ALL;
            this.matcher = null;
        } else {
            // If the pattern ends with {@code /**} and has no other wildcards or path
            // variables, then optimize to a sub-path match
            if (pattern.endsWith(MATCH_ALL)
                    && (pattern.indexOf('?') == -1 && pattern.indexOf('{') == -1 && pattern.indexOf('}') == -1)
                    && pattern.indexOf("*") == pattern.length() - 2) {
                this.matcher = new SubpathMatcher(pattern.substring(0, pattern.length() - 3), caseSensitive);
            } else {
                this.matcher = new IhaAntMatcher(pattern, caseSensitive);
            }
        }
        this.pattern = pattern;
        this.httpMethod = StringUtil.hasText(httpMethod) ? httpMethod.toUpperCase() : null;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        if (this.httpMethod != null && StringUtil.hasText(request.getMethod())
                && StringUtil.equals(this.httpMethod, request.getMethod().toUpperCase())) {
            return false;
        }
        if (this.pattern.equals(MATCH_ALL)) {
            return true;
        }
        String url = getRequestPath(request);
        return this.matcher.matches(url);
    }

    private String getRequestPath(HttpServletRequest request) {
        String url = request.getServletPath();
        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            url = StringUtil.hasLength(url) ? url + pathInfo : pathInfo;
        }
        return url;
    }

    private interface Matcher {
        boolean matches(String path);

        Map<String, String> extractUriTemplateVariables(String path);
    }

    private static final class IhaAntMatcher implements Matcher {

        private final AntPathMatcher antMatcher;

        private final String pattern;

        private IhaAntMatcher(String pattern, boolean caseSensitive) {
            this.pattern = pattern;
            this.antMatcher = createMatcher(caseSensitive);
        }

        @Override
        public boolean matches(String path) {
            return this.antMatcher.match(this.pattern, path);
        }

        @Override
        public Map<String, String> extractUriTemplateVariables(String path) {
            return this.antMatcher.extractUriTemplateVariables(this.pattern, path);
        }

        private static AntPathMatcher createMatcher(boolean caseSensitive) {
            AntPathMatcher matcher = new AntPathMatcher();
            matcher.setTrimTokens(false);
            matcher.setCaseSensitive(caseSensitive);
            return matcher;
        }
    }

    /**
     * Optimized matcher for trailing wildcards
     */
    private static final class SubpathMatcher implements Matcher {

        private final String subpath;

        private final int length;

        private final boolean caseSensitive;

        private SubpathMatcher(String subpath, boolean caseSensitive) {
            Assert.isTrue(!subpath.contains("*"), "subpath cannot contain \"*\"");
            this.subpath = caseSensitive ? subpath : subpath.toLowerCase();
            this.length = subpath.length();
            this.caseSensitive = caseSensitive;
        }

        @Override
        public boolean matches(String path) {
            if (!this.caseSensitive) {
                path = path.toLowerCase();
            }
            return path.startsWith(this.subpath) && (path.length() == this.length || path.charAt(this.length) == '/');
        }

        @Override
        public Map<String, String> extractUriTemplateVariables(String path) {
            return Collections.emptyMap();
        }

    }
}
