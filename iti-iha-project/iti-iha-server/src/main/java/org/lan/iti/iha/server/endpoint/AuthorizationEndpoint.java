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

import cn.hutool.core.util.ArrayUtil;
import org.lan.iti.common.core.util.StringUtil;
import org.lan.iti.common.extension.ExtensionLoader;
import org.lan.iti.iha.core.result.IhaResponse;
import org.lan.iti.iha.oauth2.util.ClientCertificateUtil;
import org.lan.iti.iha.security.IhaSecurity;
import org.lan.iti.iha.security.clientdetails.ClientDetails;
import org.lan.iti.iha.security.userdetails.UserDetails;
import org.lan.iti.iha.server.IhaServerConstants;
import org.lan.iti.iha.server.exception.InvalidScopeException;
import org.lan.iti.iha.server.model.enums.ErrorResponse;
import org.lan.iti.iha.server.model.enums.ResponseType;
import org.lan.iti.iha.server.provider.AuthorizationProvider;
import org.lan.iti.iha.server.security.IhaServerRequestParam;
import org.lan.iti.iha.server.util.EndpointUtil;
import org.lan.iti.iha.server.util.OAuth2Util;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
public class AuthorizationEndpoint extends AbstractEndpoint {
    /**
     * Authorize current HTTP request
     * <p>
     * When logged in, the method returns the callback url (with parameters such as code)
     * <p>
     * When not logged in, the method returns the login url (with the parameters of the current HTTP request)
     *
     * @param request current HTTP request
     * @return Callback url or authorization url
     * @throws IOException IOException
     */
    public IhaResponse authorize(HttpServletRequest request) throws IOException {
        // TODO SecurityContextHolder 要与 Cache 结合使用，可能需要添加一个过滤器用于处理此需求，在过滤器中读取缓存并保存至ThreadLocal中
        IhaServerRequestParam param = new IhaServerRequestParam(request);
        param.setClient(ClientCertificateUtil.getClientCertificate(request));

        ClientDetails clientDetail = IhaSecurity.getContext().getClientDetailsService().getByClientId(param.getClientId());

        OAuth2Util.validClientDetail(clientDetail);
        OAuth2Util.validateResponseType(param.getResponseType(), clientDetail.getResponseTypes());
        OAuth2Util.validateRedirectUri(param.getRedirectUri(), clientDetail);
        OAuth2Util.validateScope(param.getScope(), clientDetail.getScopes());

        if (IhaSecurity.isAuthenticated()) {
            UserDetails userDetails = IhaSecurity.getUser();
            String url = generateResponseUrl(param, param.getResponseType(), clientDetail, userDetails, EndpointUtil.getIssuer(request));
            return IhaResponse.ok(url);
        }
        return IhaResponse.ok(OAuth2Util.createAuthorizeUrl(EndpointUtil.getLoginPageUrl(request), param));
    }

    /**
     * User-initiated consent authorization
     *
     * @param request current HTTP request
     * @return Return the callback url (with parameters such as code)
     */
    public IhaResponse agree(HttpServletRequest request) {
        IhaServerRequestParam param = new IhaServerRequestParam(request);

        // The scope checked by the user may be inconsistent with the scope passed in the current HTTP request
        String[] requestScopes = request.getParameterValues("scopes");
        Set<String> scopes;
        if (ArrayUtil.isEmpty(requestScopes)) {
            if (StringUtil.isEmpty(param.getScope())) {
                throw new InvalidScopeException(ErrorResponse.INVALID_SCOPE);
            }
            scopes = new TreeSet<>(StringUtil.split(param.getScope(), IhaServerConstants.SPACE));
        } else {
            scopes = new TreeSet<>(Arrays.asList(requestScopes));
        }
        // Ultimately participating in the authorized scope
        param.setScope(String.join(" ", scopes));

        param.setClient(ClientCertificateUtil.getClientCertificate(request));
        ClientDetails clientDetail = IhaSecurity.getContext().getClientDetailsService().getByClientId(param.getClientId());
        OAuth2Util.validClientDetail(clientDetail);

        String responseType = param.getResponseType();
        UserDetails userDetails = IhaSecurity.getUser();
        String url = generateResponseUrl(param, responseType, clientDetail, userDetails, EndpointUtil.getIssuer(request));
        return IhaResponse.ok(url);
    }

    /**
     * Generate callback url
     *
     * @param param         Parameters in the current HTTP request
     * @param responseType  oauth authorized response type
     * @param clientDetails Currently authorized client
     * @return Callback url
     */
    private String generateResponseUrl(IhaServerRequestParam param, String responseType, ClientDetails clientDetails, UserDetails userDetails, String issuer) {
        ExtensionLoader<AuthorizationProvider, String> loader = ExtensionLoader.getLoader(AuthorizationProvider.class);
        AuthorizationProvider provider = loader.getFirst(responseType);
        if (provider == null) {
            // none
            provider = loader.getFirst(ResponseType.NONE.getType());
        }
        return provider.generateRedirect(param, responseType, clientDetails, userDetails, issuer);
    }
}
