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

import org.lan.iti.iha.security.authentication.Authentication;

import java.io.Serializable;

/**
 * Interface defining the minimum security information associated with the current thread
 * of execution.
 *
 * @author NorthLan
 * @date 2021/7/29
 * @url https://blog.noahlan.com
 */
public interface SecurityContext extends Serializable {

    /**
     * Obtains the currently authenticated principal, or an authentication request token.
     *
     * @return the <code>Authentication</code> or <code>null</code> if no authentication
     * information is available
     */
    Authentication getAuthentication();

    /**
     * Changes the currently authenticated principal, or removes the authentication
     * information.
     *
     * @param authentication the new <code>Authentication</code> token, or
     *                       <code>null</code> if no further authentication information should be stored
     */
    void setAuthentication(Authentication authentication);
}
