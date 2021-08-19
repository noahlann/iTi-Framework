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

import org.lan.iti.iha.security.IhaSecurityConstants;
import org.lan.iti.iha.server.service.UserStoreService;
import org.lan.iti.iha.security.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 基于Session的用户数据持久化服务
 *
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
public class SessionUserStoreService implements UserStoreService {

    @Override
    public String save(UserDetails userDetails, HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        session.setAttribute(IhaSecurityConstants.USER_DETAILS_CACHE_KEY, userDetails);
        return session.getId();
    }

    @Override
    public UserDetails get(HttpServletRequest request, HttpServletResponse response) {
        return (UserDetails) request.getSession(false).getAttribute(IhaSecurityConstants.USER_DETAILS_CACHE_KEY);
    }

    @Override
    public void remove(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        session.removeAttribute(IhaSecurityConstants.USER_DETAILS_CACHE_KEY);
        session.invalidate();
    }
}
