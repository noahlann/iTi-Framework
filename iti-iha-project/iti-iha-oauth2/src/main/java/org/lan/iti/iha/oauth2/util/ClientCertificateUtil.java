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

package org.lan.iti.iha.oauth2.util;

import lombok.experimental.UtilityClass;
import org.lan.iti.common.core.util.StringUtil;
import org.lan.iti.iha.security.basic.BasicCredentials;
import org.lan.iti.iha.security.util.RequestUtil;
import org.lan.iti.iha.oauth2.ClientCertificate;
import org.lan.iti.iha.oauth2.OAuth2ParameterNames;
import org.lan.iti.iha.oauth2.enums.ClientAuthenticationMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * client_id:client_secret
 * <p>
 * * parameters: ?client_id=xxx&client_secret=xxxx
 * * header: Authorization: base64(clientId:clientSecret)
 *
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@UtilityClass
public class ClientCertificateUtil {
    public static ClientCertificate getClientCertificate(HttpServletRequest request) {
        ClientCertificate clientCertificate = getClientCertificateFromRequestParameter(request);
        if (StringUtil.isEmpty(clientCertificate.getId())) {
            clientCertificate = getClientCertificateFromHeader(request);
        }
        return clientCertificate;
    }

    private static ClientCertificate getClientCertificateFromRequestParameter(HttpServletRequest request) {
        String clientId = RequestUtil.getParam(OAuth2ParameterNames.CLIENT_ID, request);
        String clientSecret = RequestUtil.getParam(OAuth2ParameterNames.CLIENT_SECRET, request);
        return new ClientCertificate(clientId, clientSecret, ClientAuthenticationMethod.CLIENT_SECRET_POST.getMethod());
    }

    private static ClientCertificate getClientCertificateFromHeader(HttpServletRequest request) {
        String authorizationHeader = RequestUtil.getHeader(OAuth2ParameterNames.AUTHORIZATION_HEADER_NAME, request);
        if (StringUtil.isNotEmpty(authorizationHeader)) {
            BasicCredentials credentials = BasicCredentials.parse(authorizationHeader);
            return new ClientCertificate(credentials);
        }
        return new ClientCertificate();
    }
}
