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

package org.lan.iti.iha.security.pipeline;

import org.lan.iti.iha.security.authentication.Authentication;
import org.lan.iti.iha.security.exception.authentication.AuthenticationException;
import org.lan.iti.iha.security.mgt.RequestParameter;

/**
 * Strategy used to handle a successful user authentication.
 *
 * @author NorthLan
 * @date 2021/7/29
 * @url https://blog.noahlan.com
 */
public interface AuthenticationSuccessHandler extends Pipeline {
    /**
     * Called when a user has been successfully authenticated.
     *
     * @param parameter      the parameter successful authentication
     * @param authentication the <tt>Authentication</tt> object which was created during
     *                       the authentication process.
     */
    void onAuthenticationSuccess(RequestParameter parameter, Authentication authentication) throws AuthenticationException;
}
