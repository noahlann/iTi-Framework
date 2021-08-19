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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Used to pass the incoming request to
 * {@link SecurityContextRepository#loadContext(HttpRequestResponseHolder)}, allowing the
 * method to swap the request for a wrapped version, as well as returning the
 * <tt>SecurityContext</tt> value.
 *
 * @author NorthLan
 * @date 2021/8/19
 * @url https://blog.noahlan.com
 */
public final class HttpRequestResponseHolder {
    private HttpServletRequest request;

    private HttpServletResponse response;

    public HttpRequestResponseHolder(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return this.response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
}
