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

package org.lan.iti.iha.server.util;

import cn.hutool.core.util.ObjectUtil;
import lombok.experimental.UtilityClass;
import org.lan.iti.iha.core.util.RequestUtil;
import org.lan.iti.iha.oauth2.OAuth2ParameterNames;
import org.lan.iti.iha.server.IhaServer;
import org.lan.iti.iha.server.IhaServerConstants;
import org.lan.iti.iha.server.model.ClientCertificate;
import org.lan.iti.iha.server.model.enums.ClientAuthenticationMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@UtilityClass
public class ClientCertificateUtil {
    public static ClientCertificate getClientCertificate(HttpServletRequest request) {
        List<ClientAuthenticationMethod> clientSecretAuthMethods = IhaServer.getIhaServerConfig().getClientAuthenticationMethods();
        if (ObjectUtil.isEmpty(clientSecretAuthMethods)) {
            clientSecretAuthMethods = Collections.singletonList(ClientAuthenticationMethod.ALL);
        }

        if (clientSecretAuthMethods.contains(ClientAuthenticationMethod.ALL)) {
            ClientCertificate clientCertificate = getClientCertificateFromRequestParameter(request);
            if (StringUtil.isEmpty(clientCertificate.getId())) {
                clientCertificate = getClientCertificateFromHeader(request);
            }
            return clientCertificate;

        } else {
            if (clientSecretAuthMethods.contains(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                    || clientSecretAuthMethods.contains(ClientAuthenticationMethod.NONE)) {
                return getClientCertificateFromRequestParameter(request);
            }

            if (clientSecretAuthMethods.contains(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)) {
                return getClientCertificateFromHeader(request);
            }
        }
        return new ClientCertificate();
    }

    private static ClientCertificate getClientCertificateFromRequestParameter(HttpServletRequest request) {
        String clientId = RequestUtil.getParam(OAuth2ParameterNames.CLIENT_ID, request);
        String clientSecret = RequestUtil.getParam(OAuth2ParameterNames.CLIENT_SECRET, request);
        return new ClientCertificate(clientId, clientSecret);
    }

    private static ClientCertificate getClientCertificateFromHeader(HttpServletRequest request) {
        String authorizationHeader = RequestUtil.getHeader(IhaServerConstants.AUTHORIZATION_HEADER_NAME, request);
        if (StringUtil.isNotEmpty(authorizationHeader)) {
            BasicCredentials credentials = BasicCredentials.parse(authorizationHeader);
            return credentials.getClientCertificate();
        }
        return new ClientCertificate();
    }
}
