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
import org.lan.iti.iha.server.IhaServer;
import org.lan.iti.iha.server.exception.InvalidScopeException;
import org.lan.iti.iha.server.model.ClientDetails;
import org.lan.iti.iha.server.model.IhaServerRequestParam;
import org.lan.iti.iha.server.model.IhaServerResponse;
import org.lan.iti.iha.server.model.UserDetails;
import org.lan.iti.iha.server.model.enums.ErrorResponse;
import org.lan.iti.iha.server.model.enums.ResponseType;
import org.lan.iti.iha.server.provider.AuthorizationProvider;
import org.lan.iti.iha.server.provider.RequestParamProvider;
import org.lan.iti.iha.server.util.EndpointUtil;
import org.lan.iti.iha.server.util.OAuthUtil;
import org.lan.iti.iha.server.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
public class AuthorizationEndpoint extends AbstractEndpoint {
    private final AuthorizationProvider idsAuthorizationProvider = new AuthorizationProvider(oAuth2Service);

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
    public IhaServerResponse<String, String> authorize(HttpServletRequest request) throws IOException {
        IhaServerRequestParam param = RequestParamProvider.parseRequest(request);

        ClientDetails clientDetail = IhaServer.getContext().getClientDetailsService().getByClientId(param.getClientId());

        OAuthUtil.validClientDetail(clientDetail);
        OAuthUtil.validateResponseType(param.getResponseType(), clientDetail.getResponseTypes());
        OAuthUtil.validateRedirectUri(param.getRedirectUri(), clientDetail);
        OAuthUtil.validateScope(param.getScope(), clientDetail.getScopes());

        if (IhaServer.isAuthenticated(request)) {
            UserDetails userDetails = IhaServer.getUser(request);
            String url = generateResponseUrl(param, param.getResponseType(), clientDetail, userDetails, EndpointUtil.getIssuer(request));
            return new IhaServerResponse<String, String>().data(url);
        }

        return new IhaServerResponse<String, String>()
                .data(OAuthUtil.createAuthorizeUrl(EndpointUtil.getLoginPageUrl(request), param));
    }

    /**
     * User-initiated consent authorization
     *
     * @param request current HTTP request
     * @return Return the callback url (with parameters such as code)
     */
    public IhaServerResponse<String, String> agree(HttpServletRequest request) {
        IhaServerRequestParam param = RequestParamProvider.parseRequest(request);

        // The scope checked by the user may be inconsistent with the scope passed in the current HTTP request
        String[] requestScopes = request.getParameterValues("scopes");
        Set<String> scopes = null;
        if (ArrayUtil.isEmpty(requestScopes)) {
            if (StringUtil.isEmpty(param.getScope())) {
                throw new InvalidScopeException(ErrorResponse.INVALID_SCOPE);
            }
            scopes = StringUtil.convertStrToList(param.getScope()).stream().distinct().collect(Collectors.toSet());
        } else {
            scopes = new TreeSet<>(Arrays.asList(requestScopes));
        }
        // Ultimately participating in the authorized scope
        param.setScope(String.join(" ", scopes));

        ClientDetails clientDetail = IhaServer.getContext().getClientDetailsService().getByClientId(param.getClientId());
        OAuthUtil.validClientDetail(clientDetail);

        String responseType = param.getResponseType();
        UserDetails userDetails = IhaServer.getUser(request);
        String url = generateResponseUrl(param, responseType, clientDetail, userDetails, EndpointUtil.getIssuer(request));
        return new IhaServerResponse<String, String>().data(url);
    }

    /**
     * Generate callback url
     *
     * @param param        Parameters in the current HTTP request
     * @param responseType oauth authorized response type
     * @param clientDetail Currently authorized client
     * @return Callback url
     */
    private String generateResponseUrl(IhaServerRequestParam param, String responseType, ClientDetails clientDetail, UserDetails userDetails, String issuer) {
        if (ResponseType.CODE.getType().equalsIgnoreCase(responseType)) {
            return idsAuthorizationProvider.generateAuthorizationCodeResponse(userDetails, param, clientDetail);
        }
        if (ResponseType.TOKEN.getType().equalsIgnoreCase(responseType)) {
            return idsAuthorizationProvider.generateImplicitGrantResponse(userDetails, param, clientDetail, issuer);
        }
        if (ResponseType.ID_TOKEN.getType().equalsIgnoreCase(responseType)) {
            return idsAuthorizationProvider.generateIdTokenAuthorizationResponse(userDetails, param, clientDetail, issuer);
        }
        if (ResponseType.ID_TOKEN_TOKEN.getType().equalsIgnoreCase(responseType)) {
            return idsAuthorizationProvider.generateIdTokenTokenAuthorizationResponse(userDetails, param, clientDetail, issuer);
        }
        if (ResponseType.CODE_ID_TOKEN.getType().equalsIgnoreCase(responseType)) {
            return idsAuthorizationProvider.generateCodeIdTokenAuthorizationResponse(userDetails, param, clientDetail, issuer);
        }
        if (ResponseType.CODE_TOKEN.getType().equalsIgnoreCase(responseType)) {
            return idsAuthorizationProvider.generateCodeTokenAuthorizationResponse(userDetails, param, clientDetail, issuer);
        }
        if (ResponseType.CODE_ID_TOKEN_TOKEN.getType().equalsIgnoreCase(responseType)) {
            return idsAuthorizationProvider.generateCodeIdTokenTokenAuthorizationResponse(userDetails, param, clientDetail, issuer);
        }
        // none
        return idsAuthorizationProvider.generateNoneAuthorizationResponse(param);
    }
}
