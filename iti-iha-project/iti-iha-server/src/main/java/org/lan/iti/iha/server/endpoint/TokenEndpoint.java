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

import org.lan.iti.common.extension.ExtensionLoader;
import org.lan.iti.iha.core.result.IhaResponse;
import org.lan.iti.iha.server.exception.UnsupportedGrantTypeException;
import org.lan.iti.iha.server.security.IhaServerRequestParam;
import org.lan.iti.iha.server.model.enums.ErrorResponse;
import org.lan.iti.iha.server.provider.AuthorizationTokenProvider;
import org.lan.iti.iha.oauth2.util.ClientCertificateUtil;
import org.lan.iti.iha.server.util.TokenUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
public class TokenEndpoint extends AbstractEndpoint {

    public IhaResponse getToken(HttpServletRequest request) {
        IhaServerRequestParam param = new IhaServerRequestParam(request);
        param.setClient(ClientCertificateUtil.getClientCertificate(request));

        AuthorizationTokenProvider provider = ExtensionLoader.getLoader(AuthorizationTokenProvider.class)
                .getFirst(param.getGrantType());
        if (provider == null) {
            throw new UnsupportedGrantTypeException(ErrorResponse.UNSUPPORTED_GRANT_TYPE);
        }
        return provider.generateResponse(param);
    }

    public IhaResponse revokeToken(HttpServletRequest request) {
        TokenUtil.invalidateToken(request);
        return IhaResponse.ok();
    }
}
