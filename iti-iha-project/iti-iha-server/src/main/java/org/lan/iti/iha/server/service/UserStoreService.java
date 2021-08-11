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

package org.lan.iti.iha.server.service;

import org.lan.iti.common.extension.IExtension;
import org.lan.iti.common.extension.annotation.Extension;
import org.lan.iti.iha.security.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * When the user logs in, store and operate the user's login information
 *
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@Extension
public interface UserStoreService extends IExtension<Object> {
    @Override
    default boolean matches(Object params) {
        return true;
    }

    /**
     * Save user data, and store user information in {@link javax.servlet.http.HttpSession} by default.
     * <p>
     * Developers can implement this method to save user information in other media, such as cache, database, etc.
     *
     * @param userDetails    User information after login
     * @param request current HTTP request
     */
    void save(UserDetails userDetails, HttpServletRequest request);

    /**
     * Get logged-in user information
     *
     * @param request current HTTP request
     * @return User
     */
    UserDetails get(HttpServletRequest request);

    /**
     * Delete logged-in user information
     *
     * @param request current HTTP request
     */
    void remove(HttpServletRequest request);
}
