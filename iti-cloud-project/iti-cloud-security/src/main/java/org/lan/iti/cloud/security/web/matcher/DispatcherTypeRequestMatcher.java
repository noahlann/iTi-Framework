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

import org.lan.iti.common.core.util.StringUtil;
import org.lan.iti.iha.security.matcher.RequestMatcher;
import org.springframework.http.HttpMethod;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;

/**
 * @author NorthLan
 * @date 2021/10/15
 * @url https://blog.noahlan.com
 */
public final class DispatcherTypeRequestMatcher implements RequestMatcher {
    private final DispatcherType dispatcherType;
    private final HttpMethod httpMethod;

    /**
     * Creates an instance which matches requests with the provided {@link DispatcherType}
     *
     * @param dispatcherType the type to match against
     */
    public DispatcherTypeRequestMatcher(DispatcherType dispatcherType) {
        this(dispatcherType, null);
    }

    /**
     * Creates an instance which matches requests with the provided {@link DispatcherType}
     * and {@link HttpMethod}
     *
     * @param dispatcherType the type to match against
     * @param httpMethod     the HTTP method to match. May be null to match all methods.
     */
    public DispatcherTypeRequestMatcher(DispatcherType dispatcherType, HttpMethod httpMethod) {
        this.dispatcherType = dispatcherType;
        this.httpMethod = httpMethod;
    }

    /**
     * Performs the match against the request's method and dispatcher type.
     *
     * @param request the request to check for a match
     * @return true if the http method and dispatcher type align
     */
    @Override
    public boolean matches(HttpServletRequest request) {
        if (this.httpMethod != null && StringUtil.hasText(request.getMethod())
                && this.httpMethod != HttpMethod.resolve(request.getMethod())) {
            return false;
        }
        return this.dispatcherType == request.getDispatcherType();
    }

    @Override
    public String toString() {
        return "DispatcherTypeRequestMatcher{" + "dispatcherType=" + this.dispatcherType + ", httpMethod="
                + this.httpMethod + '}';
    }
}
