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

package org.lan.iti.iha.server.service.impl;

import org.lan.iti.iha.security.userdetails.UserDetails;
import org.lan.iti.iha.server.IhaServerConstants;
import org.lan.iti.iha.server.service.UserStoreService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
public class UserStoreServiceImpl implements UserStoreService {
    @Override
    public void save(UserDetails userDetails, HttpServletRequest request) {
        request.getSession().setAttribute(IhaServerConstants.OAUTH_USER_CACHE_KEY, userDetails);
    }

    @Override
    public UserDetails get(HttpServletRequest request) {
        return (UserDetails) request.getSession().getAttribute(IhaServerConstants.OAUTH_USER_CACHE_KEY);
    }

    @Override
    public void remove(HttpServletRequest request) {
        request.getSession().removeAttribute(IhaServerConstants.OAUTH_USER_CACHE_KEY);
    }
}
