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

package org.lan.iti.iha.server.provider.token;

import org.lan.iti.iha.oauth2.GrantType;
import org.lan.iti.iha.oauth2.OAuth2ParameterNames;
import org.lan.iti.iha.security.IhaSecurity;
import org.lan.iti.iha.security.authentication.Authentication;
import org.lan.iti.iha.security.clientdetails.ClientDetails;
import org.lan.iti.iha.security.exception.authentication.AuthenticationException;
import org.lan.iti.iha.security.userdetails.UserDetails;
import org.lan.iti.iha.server.IhaServer;
import org.lan.iti.iha.server.model.AuthorizationToken;
import org.lan.iti.iha.server.model.AuthorizationTokenHelper;
import org.lan.iti.iha.server.security.IhaServerRequestParam;
import org.lan.iti.iha.server.util.AuthorizationTokenUtil;
import org.lan.iti.iha.server.util.EndpointUtil;
import org.lan.iti.iha.server.util.OAuth2Util;
import org.lan.iti.iha.simple.IhaSimple;
import org.lan.iti.iha.simple.SimpleConfig;
import org.lan.iti.iha.simple.security.SimpleRequestParameter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * RFC6749 4.3.  Resource Owner Password Credentials Grant
 *
 * @author NorthLan
 * @date 2021/8/2
 * @url https://blog.noahlan.com
 * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.3" target="_blank">4.3.  Resource Owner Password Credentials Grant</a>
 */
public class ResourceOwnerPasswordProcessor extends AbstractAuthorizationTokenProcessor {
    @Override
    public boolean matches(String params) {
        return GrantType.PASSWORD.getType().equals(params);
    }

    @Override
    protected Map<String, Object> processor(IhaServerRequestParam param) {
        HttpServletRequest request = param.getRequest();
        HttpServletResponse response = param.getResponse();

        SimpleConfig config = IhaSimple.getConfig().toBuilder().build();
        config.setPrincipalField(OAuth2ParameterNames.USERNAME)
                .setCredentialsField(OAuth2ParameterNames.PASSWORD);

        SimpleRequestParameter simpleRequestParameter = new SimpleRequestParameter(param);
        simpleRequestParameter.setConfig(config);
        simpleRequestParameter.put(config.getTypeField(), "username");

        Authentication authentication = IhaSecurity.getSecurityManager().authenticate(simpleRequestParameter);
        if (!authentication.isAuthenticated()) {
            throw new AuthenticationException("error");
        }
        UserDetails userDetails = IhaSecurity.getUser(null);
        IhaServer.getContext().getUserStoreService().save(userDetails, request, response);

        ClientDetails clientDetails = IhaSecurity.getContext().getClientDetailsService().getByClientId(param.getClientId());
        String requestScope = param.getScope();

        OAuth2Util.validClientDetails(clientDetails);
        OAuth2Util.validateScope(requestScope, clientDetails.getScopes());
        OAuth2Util.validateGrantType(param.getGrantType(), clientDetails.getGrantTypes(), GrantType.PASSWORD);
        OAuth2Util.validateSecret(param, clientDetails);

        AuthorizationToken authorizationToken = AuthorizationTokenUtil.createAccessToken(
                userDetails,
                clientDetails,
                param.getGrantType(),
                requestScope,
                param.getNonce(),
                EndpointUtil.getIssuer(request),
                true);

        return AuthorizationTokenHelper.toMap(authorizationToken);
    }
}