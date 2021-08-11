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

package org.lan.iti.iha.server.endpoint;

import org.lan.iti.iha.core.result.IhaResponse;
import org.lan.iti.iha.server.IhaServer;
import org.lan.iti.iha.server.util.EndpointUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
public class LogoutEndpoint extends AbstractEndpoint {

    public IhaResponse logout(HttpServletRequest request) {
        IhaServer.removeUser(request);
        request.getSession().invalidate();
        return IhaResponse.ok(EndpointUtil.getLogoutRedirectUrl(request));
    }
}
