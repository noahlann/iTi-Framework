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

import com.xkcoding.json.util.StringUtil;
import org.lan.iti.iha.oauth2.GrantType;
import org.lan.iti.iha.server.exception.UnsupportedGrantTypeException;
import org.lan.iti.iha.server.model.IhaServerRequestParam;
import org.lan.iti.iha.server.model.IhaServerResponse;
import org.lan.iti.iha.server.model.enums.ErrorResponse;
import org.lan.iti.iha.server.provider.AuthorizationTokenProvider;
import org.lan.iti.iha.server.provider.RequestParamProvider;
import org.lan.iti.iha.server.util.TokenUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
public class TokenEndpoint extends AbstractEndpoint {
    private final AuthorizationTokenProvider authorizationTokenProvider = new AuthorizationTokenProvider(oAuth2Service);

    public IhaServerResponse<String, Object> getToken(HttpServletRequest request) {
        IhaServerRequestParam param = RequestParamProvider.parseRequest(request);

        if (StringUtil.isEmpty(param.getGrantType())) {
            throw new UnsupportedGrantTypeException(ErrorResponse.UNSUPPORTED_GRANT_TYPE);
        }
        if (GrantType.AUTHORIZATION_CODE.getType().equals(param.getGrantType())) {
            return authorizationTokenProvider.generateAuthorizationCodeResponse(param, request);
        }
        if (GrantType.PASSWORD.getType().equals(param.getGrantType())) {
            return authorizationTokenProvider.generatePasswordResponse(param, request);
        }
        if (GrantType.CLIENT_CREDENTIALS.getType().equals(param.getGrantType())) {
            return authorizationTokenProvider.generateClientCredentialsResponse(param, request);
        }
        if (GrantType.REFRESH_TOKEN.getType().equals(param.getGrantType())) {
            return authorizationTokenProvider.generateRefreshTokenResponse(param, request);
        }
        throw new UnsupportedGrantTypeException(ErrorResponse.UNSUPPORTED_GRANT_TYPE);
    }

    public IhaServerResponse<String, Object> revokeToken(HttpServletRequest request) {
        TokenUtil.invalidateToken(request);
        return new IhaServerResponse<>();
    }
}
