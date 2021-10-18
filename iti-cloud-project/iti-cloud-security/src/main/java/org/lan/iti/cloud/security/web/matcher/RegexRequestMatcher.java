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

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.util.StringUtil;
import org.lan.iti.iha.security.matcher.RequestMatcher;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 * @author NorthLan
 * @date 2021/10/15
 * @url https://blog.noahlan.com
 */
@Slf4j
public final class RegexRequestMatcher implements RequestMatcher {
    private static final int DEFAULT = 0;

    private final Pattern pattern;

    private final HttpMethod httpMethod;

    /**
     * Creates a case-sensitive {@code Pattern} instance to match against the request.
     *
     * @param pattern    the regular expression to compile into a pattern.
     * @param httpMethod the HTTP method to match. May be null to match all methods.
     */
    public RegexRequestMatcher(String pattern, String httpMethod) {
        this(pattern, httpMethod, false);
    }

    /**
     * As above, but allows setting of whether case-insensitive matching should be used.
     *
     * @param pattern         the regular expression to compile into a pattern.
     * @param httpMethod      the HTTP method to match. May be null to match all methods.
     * @param caseInsensitive if true, the pattern will be compiled with the
     *                        {@link Pattern#CASE_INSENSITIVE} flag set.
     */
    public RegexRequestMatcher(String pattern, String httpMethod, boolean caseInsensitive) {
        this.pattern = Pattern.compile(pattern, caseInsensitive ? Pattern.CASE_INSENSITIVE : DEFAULT);
        this.httpMethod = StringUtil.hasText(httpMethod) ? HttpMethod.valueOf(httpMethod) : null;
    }

    /**
     * Performs the match of the request URL ({@code servletPath + pathInfo + queryString}
     * ) against the compiled pattern. If the query string is present, a question mark
     * will be prepended.
     *
     * @param request the request to match
     * @return true if the pattern matches the URL, false otherwise.
     */
    @Override
    public boolean matches(HttpServletRequest request) {
        if (this.httpMethod != null && request.getMethod() != null
                && this.httpMethod != HttpMethod.resolve(request.getMethod())) {
            return false;
        }
        String url = request.getServletPath();
        String pathInfo = request.getPathInfo();
        String query = request.getQueryString();
        if (pathInfo != null || query != null) {
            StringBuilder sb = new StringBuilder(url);
            if (pathInfo != null) {
                sb.append(pathInfo);
            }
            if (query != null) {
                sb.append('?').append(query);
            }
            url = sb.toString();
        }
        log.debug(String.format("Checking match of request : '%s'; against '%s'", url, this.pattern));
        return this.pattern.matcher(url).matches();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Regex [pattern='").append(this.pattern).append("'");
        if (this.httpMethod != null) {
            sb.append(", ").append(this.httpMethod);
        }
        sb.append("]");
        return sb.toString();
    }
}
