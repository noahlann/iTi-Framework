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
 * @author NorthLan
 * @date 2021/8/19
 * @url https://blog.noahlan.com
 */
public interface SecurityContextRepository {
    /**
     * Obtains the security context for the supplied request. For an unauthenticated user,
     * an empty context implementation should be returned. This method should not return
     * null.
     *
     * @param holder holder for the current request and response for which
     *               the context should be loaded.
     * @return The security context which should be used for the current request, never
     * null.
     */
    SecurityContext loadContext(HttpRequestResponseHolder holder);

    /**
     * Stores the security context on completion of a request.
     *
     * @param context  the non-null context which was obtained from the holder.
     * @param request  the current request
     * @param response the current response
     */
    void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response);

    /**
     * Allows the repository to be queried as to whether it contains a security context
     * for the current request.
     *
     * @param request the current request
     * @return true if a context is found for the request, false otherwise
     */
    boolean containsContext(HttpServletRequest request);
}
