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

package org.lan.iti.cloud.iha.core.store;

import org.lan.iti.cloud.iha.core.IhaUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
public interface IhaUserStore {
    /**
     * Login completed, save user information to the cache
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @param ihaUser  User information after successful login
     * @return IhaUser
     */
    IhaUser save(HttpServletRequest request, HttpServletResponse response, IhaUser ihaUser);

    /**
     * Clear user information from cache
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     */
    void remove(HttpServletRequest request, HttpServletResponse response);

    /**
     * Get the login user information from the cache, return {@code IhaUser} if it exists,
     * return {@code null} if it is not logged in or the login has expired
     *
     * @param request  current HTTP request
     * @param response current HTTP response
     * @return IhaUser
     */
    IhaUser get(HttpServletRequest request, HttpServletResponse response);
}
