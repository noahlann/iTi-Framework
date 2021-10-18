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

import org.lan.iti.iha.security.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

/**
 * Matches any supplied request.
 *
 * @author NorthLan
 * @date 2021/10/15
 * @url https://blog.noahlan.com
 */
public final class AnyRequestMatcher implements RequestMatcher {

    public static final RequestMatcher INSTANCE = new AnyRequestMatcher();

    private AnyRequestMatcher() {
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return true;
    }

    @SuppressWarnings({"ConditionCoveredByFurtherCondition", "DuplicateCondition", "ConstantConditions"})
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AnyRequestMatcher
                || obj instanceof org.lan.iti.cloud.security.web.matcher.AnyRequestMatcher;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public String toString() {
        return "any request";
    }
}
