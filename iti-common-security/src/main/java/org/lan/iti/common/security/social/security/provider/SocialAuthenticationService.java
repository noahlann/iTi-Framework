/*
 *
 *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.common.security.social.security.provider;

import org.lan.iti.common.security.social.security.SocialAuthenticationToken;
import org.lan.iti.common.security.social.connect.ConnectionFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 社交身份鉴权服务
 *
 * @author NorthLan
 * @date 2020-03-17
 * @url https://noahlan.com
 */
public interface SocialAuthenticationService<S> {

    /**
     * @return 用于认证的 {@link ConnectionFactory}
     */
    ConnectionFactory<S> getConnectionFactory();

    /**
     * 获取 AuthToken
     *
     * @param request  当前 {@link HttpServletRequest}
     * @param response 当前 {@link HttpServletResponse}
     * @return 未授权的Token或null
     */
    SocialAuthenticationToken getAuthToken(HttpServletRequest request, HttpServletResponse response);
}
