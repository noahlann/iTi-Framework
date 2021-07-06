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

package org.lan.iti.cloud.iha.server.provider;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import org.lan.iti.cloud.iha.oauth2.OAuth2ParameterNames;
import org.lan.iti.cloud.iha.server.IhaServer;
import org.lan.iti.cloud.iha.server.config.IhaServerConfig;
import org.lan.iti.cloud.iha.server.exception.InvalidRequestException;
import org.lan.iti.cloud.iha.server.model.ClientCertificate;
import org.lan.iti.cloud.iha.server.model.IhaServerRequestParam;
import org.lan.iti.cloud.iha.server.model.enums.ErrorResponse;
import org.lan.iti.cloud.iha.server.util.ClientCertificateUtil;
import org.lan.iti.cloud.iha.server.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
public class RequestParamProvider {
    public static IhaServerRequestParam parseRequest(HttpServletRequest request) {
        Map<String, String> paramMap = ServletUtil.getParamMap(request);
        if (ObjectUtil.isEmpty(paramMap)) {
            throw new InvalidRequestException(ErrorResponse.INVALID_REQUEST);
        }

        IhaServerRequestParam param = new IhaServerRequestParam(paramMap);

        ClientCertificate clientCertificate = ClientCertificateUtil.getClientCertificate(request);

        if (StringUtil.isNotEmpty(clientCertificate.getId())) {
            param.map(OAuth2ParameterNames.CLIENT_ID, clientCertificate.getId());
        }
        if (StringUtil.isNotEmpty(clientCertificate.getSecret())) {
            param.map(OAuth2ParameterNames.CLIENT_SECRET, clientCertificate.getSecret());
        }

        // Get username and password Applies to:<a href="https://tools.ietf.org/html/rfc6749#section-4.3" target="_blank">Resource Owner Password Credentials Grant</a>
        IhaServerConfig config = IhaServer.getIhaServerConfig();
        param.setUsernameField(config.getUsernameField());
        param.setPasswordField(config.getPasswordField());

        /*
         * Applicable to open pkce enhanced protocol in authorization code mode
         * @see <a href="https://tools.ietf.org/html/rfc7636" target="_blank">Proof Key for Code Exchange by OAuth Public Clients</a>
         */
        return param;
    }
}
