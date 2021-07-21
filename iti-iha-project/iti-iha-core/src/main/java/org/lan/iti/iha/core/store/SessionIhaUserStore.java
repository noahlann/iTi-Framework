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

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import org.lan.iti.iha.core.IhaConstants;
import org.lan.iti.iha.core.IhaUser;
import org.lan.iti.iha.core.config.IhaConfig;
import org.lan.iti.iha.core.context.IhaAuthentication;
import org.lan.iti.iha.core.util.IhaTokenHelper;
import org.lan.iti.iha.core.util.IhaUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
public class SessionIhaUserStore implements IhaUserStore {

    @Override
    public IhaUser save(HttpServletRequest request, HttpServletResponse response, IhaUser ihaUser) {
        HttpSession session = request.getSession();
        IhaUser newUser = new IhaUser();
        BeanUtil.copyProperties(ihaUser, newUser, CopyOptions.create().ignoreNullValue());
        newUser.setPassword(null);
        session.setAttribute(IhaConstants.SESSION_USER_KEY, newUser);

        IhaConfig config = IhaAuthentication.getContext().getConfig();
        if (!config.isSso()) {
            String token = IhaUtil.createToken(ihaUser, request);
            IhaTokenHelper.saveUserToken(ihaUser.getId(), token);
            ihaUser.setToken(token);
        }
        return ihaUser;
    }

    @Override
    public void remove(HttpServletRequest request, HttpServletResponse response) {
        IhaConfig config = IhaAuthentication.getContext().getConfig();
        if (!config.isSso()) {
            IhaUser user = this.get(request, response);
            if (null != user) {
                IhaTokenHelper.removeUserToken(user.getId());
            }
        }

        HttpSession session = request.getSession();
        session.removeAttribute(IhaConstants.SESSION_USER_KEY);
        session.invalidate();
    }

    @Override
    public IhaUser get(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        return (IhaUser) session.getAttribute(IhaConstants.SESSION_USER_KEY);
    }
}
