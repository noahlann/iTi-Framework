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

package org.lan.iti.iha.core.store;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import org.lan.iti.iha.core.IhaUser;
import org.lan.iti.iha.core.repository.IhaUserRepository;
import org.lan.iti.iha.core.util.IhaTokenHelper;
import org.lan.iti.iha.sso.IhaSsoHelper;
import org.lan.iti.iha.sso.config.IhaSsoConfig;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@AllArgsConstructor
public class SsoIhaUserStore extends SessionIhaUserStore {
    /**
     * Abstract the user-related function interface, which is implemented by the caller business system.
     */
    protected IhaUserRepository userRepository;

    /**
     * Iha Sso configuration.
     */
    protected IhaSsoConfig ssoConfig;

    @Override
    public IhaUser save(HttpServletRequest request, HttpServletResponse response, IhaUser ihaUser) {
        String token = IhaSsoHelper.login(ihaUser.getId(), ihaUser.getUsername(), this.ssoConfig, request, response);
        super.save(request, response, ihaUser);
        IhaTokenHelper.saveUserToken(ihaUser.getId(), token);
        return ihaUser.setToken(token);
    }

    @Override
    public void remove(HttpServletRequest request, HttpServletResponse response) {
        IhaUser user = this.get(request, response);
        if (null != user) {
            IhaTokenHelper.removeUserToken(user.getId());
        }
        super.remove(request, response);
        IhaSsoHelper.logout(request, response);
    }

    @Override
    public IhaUser get(HttpServletRequest request, HttpServletResponse response) {
        String userId = IhaSsoHelper.checkLogin(request);
        if (StrUtil.isBlank(userId)) {
            // The cookie has expired. Clear session content
            super.remove(request, response);
            return null;
        }
        IhaUser sessionUser = super.get(request, response);

        /*
          1. The cookie is not invalid, but the user in the session is invalid.
            retrieve the user information and save it to the session
          2. The user information in the session is inconsistent with the user information in the cookie,
            which indicates that an endpoint logs out and then logs in again.
            At this time, the session needs to be updated
         */
        if (null == sessionUser || !sessionUser.getId().equals(userId)) {
            sessionUser = this.userRepository.getById(userId);
            // Back-to-back operation to prevent anomalies
            if (null == sessionUser) {
                return null;
            }
            // Save user information into session
            super.save(request, response, sessionUser);
            return sessionUser;
        }
        return sessionUser;
    }
}
