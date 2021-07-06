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

package org.lan.iti.cloud.iha.server.endpoint;

import org.lan.iti.cloud.iha.server.IhaServer;
import org.lan.iti.cloud.iha.server.exception.IhaServerException;
import org.lan.iti.cloud.iha.server.model.IhaServerResponse;
import org.lan.iti.cloud.iha.server.model.User;
import org.lan.iti.cloud.iha.server.pipeline.Pipeline;
import org.lan.iti.cloud.iha.server.util.EndpointUtil;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
public class LogoutEndpoint extends AbstractEndpoint {

    public IhaServerResponse<String, String> logout(HttpServletRequest request, ServletResponse response) {
        Pipeline<User> logoutPipeline = IhaServer.getContext().getLogoutPipeline();
        logoutPipeline = this.getUserInfoIdsPipeline(logoutPipeline);
        if (!logoutPipeline.preHandle(request, response)) {
            throw new IhaServerException("LogoutPipeline<User>.preHandle returns false, the process is blocked.");
        }
        IhaServer.removeUser(request);
        request.getSession().invalidate();

        logoutPipeline.afterHandle(request, response);
        return new IhaServerResponse<String, String>()
                .data(EndpointUtil.getLogoutRedirectUrl(request));
    }
}
