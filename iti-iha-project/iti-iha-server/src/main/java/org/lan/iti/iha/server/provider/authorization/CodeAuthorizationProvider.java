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

package org.lan.iti.iha.server.provider.authorization;

import org.lan.iti.common.core.util.StringUtil;
import org.lan.iti.iha.security.userdetails.UserDetails;
import org.lan.iti.iha.security.clientdetails.ClientDetails;
import org.lan.iti.iha.server.security.IhaServerRequestParam;
import org.lan.iti.iha.server.model.enums.ResponseType;
import org.lan.iti.iha.server.provider.AuthorizationProvider;
import org.lan.iti.iha.server.util.OAuth2Util;

/**
 * 4.1.  Authorization Code Grant
 *
 * @author NorthLan
 * @date 2021/8/2
 * @url https://blog.noahlan.com
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.1">4.1.  Authorization Code Grant</a>
 */
public class CodeAuthorizationProvider implements AuthorizationProvider {
    @Override
    public boolean matches(String params) {
        return ResponseType.CODE.getType().equalsIgnoreCase(params);
    }

    @Override
    public String generateRedirect(IhaServerRequestParam param, String responseType, ClientDetails clientDetails, UserDetails userDetails, String issuer) {
        String authorizationCode = OAuth2Util.createAuthorizationCode(param, userDetails, OAuth2Util.getCodeExpiresIn(clientDetails.getCodeTimeToLive()));
        String params = "?code=" + authorizationCode;
        if (StringUtil.isNotEmpty(param.getState())) {
            params = params + "&state=" + param.getState();
        }
        return param.getRedirectUri() + params;
    }
}
