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

package org.lan.iti.iha.server.provider;

import lombok.AllArgsConstructor;
import org.lan.iti.iha.oauth2.GrantType;
import org.lan.iti.iha.oauth2.OAuth2ParameterNames;
import org.lan.iti.iha.oidc.OidcParameterNames;
import org.lan.iti.iha.server.IhaServer;
import org.lan.iti.iha.server.IhaServerConstants;
import org.lan.iti.iha.server.exception.IhaServerException;
import org.lan.iti.iha.server.model.*;
import org.lan.iti.iha.server.model.enums.ErrorResponse;
import org.lan.iti.iha.server.service.OAuth2Service;
import org.lan.iti.iha.server.util.EndpointUtil;
import org.lan.iti.iha.server.util.OAuthUtil;
import org.lan.iti.iha.server.util.StringUtil;
import org.lan.iti.iha.server.util.TokenUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * The token endpoint creates a token, and returns different token information for different authorization types
 *
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@AllArgsConstructor
public class AuthorizationTokenProvider {
    private final OAuth2Service oAuth2Service;

    /**
     * RFC6749 4.1. authorization code grant
     *
     * @param param   request params
     * @param request current HTTP request
     * @return IhaServerResponse
     * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.1" target="_blank">4.1.  Authorization Code Grant</a>
     */
    public IhaServerResponse<String, Object> generateAuthorizationCodeResponse(IhaServerRequestParam param, HttpServletRequest request) {
        AuthorizationCode codeInfo = oAuth2Service.validateAndGetAuthorizationCode(param.getGrantType(), param.getCode());

        String scope = codeInfo.getScope();
        UserDetails userDetails = codeInfo.getUserDetails();
        String nonce = codeInfo.getNonce();

        ClientDetails clientDetails = IhaServer.getContext().getClientDetailsService().getByClientId(param.getClientId());

        OAuthUtil.validClientDetail(clientDetails);
        OAuthUtil.validateGrantType(param.getGrantType(), clientDetails.getGrantTypes(), GrantType.AUTHORIZATION_CODE);
        OAuthUtil.validateSecret(param, clientDetails, oAuth2Service);
        OAuthUtil.validateRedirectUri(param.getRedirectUri(), clientDetails);

        oAuth2Service.invalidateCode(param.getCode());

        long expiresIn = OAuthUtil.getAccessTokenExpiresIn(clientDetails.getAccessTokenTimeToLive());

        AccessToken accessToken = TokenUtil.createAccessToken(userDetails, clientDetails, param.getGrantType(), scope, nonce, EndpointUtil.getIssuer(request));
        IhaServerResponse<String, Object> response = new IhaServerResponse<String, Object>()
                .add(OAuth2ParameterNames.ACCESS_TOKEN, accessToken.getAccessToken())
                .add(OAuth2ParameterNames.REFRESH_TOKEN, accessToken.getRefreshToken())
                .add(OAuth2ParameterNames.EXPIRES_IN, expiresIn)
                .add(OAuth2ParameterNames.TOKEN_TYPE, IhaServerConstants.TOKEN_TYPE_BEARER)
                .add(OAuth2ParameterNames.SCOPE, scope);
        if (OAuthUtil.isOidcProtocol(scope)) {
            response.add(OidcParameterNames.ID_TOKEN, TokenUtil.createIdToken(clientDetails, userDetails, nonce, EndpointUtil.getIssuer(request)));
        }
        return response;
    }

    /**
     * RFC6749 4.3.  Resource Owner Password Credentials Grant
     *
     * @param param   request params
     * @param request current HTTP request
     * @return IhaServerResponse
     * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.3" target="_blank">4.3.  Resource Owner Password Credentials Grant</a>
     */
    public IhaServerResponse<String, Object> generatePasswordResponse(IhaServerRequestParam param, HttpServletRequest request) {
        String username = param.getUsername();
        String password = param.getPassword();
        String clientId = param.getClientId();
        UserDetails userDetails = IhaServer.getContext().getUserDetailService().loginByUsernameAndPassword(username, password, clientId);
        if (null == userDetails) {
            throw new IhaServerException(ErrorResponse.INVALID_USER_CERTIFICATE);
        }
        IhaServer.saveUser(userDetails, request);

        ClientDetails clientDetails = IhaServer.getContext().getClientDetailsService().getByClientId(param.getClientId());
        String requestScope = param.getScope();

        OAuthUtil.validClientDetail(clientDetails);
        OAuthUtil.validateScope(requestScope, clientDetails.getScopes());
        OAuthUtil.validateGrantType(param.getGrantType(), clientDetails.getGrantTypes(), GrantType.PASSWORD);
        OAuthUtil.validateSecret(param, clientDetails, oAuth2Service);

        long expiresIn = OAuthUtil.getAccessTokenExpiresIn(clientDetails.getAccessTokenTimeToLive());

        AccessToken accessToken = TokenUtil.createAccessToken(userDetails, clientDetails, param.getGrantType(), requestScope, param.getNonce(), EndpointUtil.getIssuer(request));
        IhaServerResponse<String, Object> response = new IhaServerResponse<String, Object>()
                .add(OAuth2ParameterNames.ACCESS_TOKEN, accessToken.getAccessToken())
                .add(OAuth2ParameterNames.REFRESH_TOKEN, accessToken.getRefreshToken())
                .add(OAuth2ParameterNames.EXPIRES_IN, expiresIn)
                .add(OAuth2ParameterNames.TOKEN_TYPE, IhaServerConstants.TOKEN_TYPE_BEARER)
                .add(OAuth2ParameterNames.SCOPE, requestScope);

        if (OAuthUtil.isOidcProtocol(requestScope)) {
            response.add(OidcParameterNames.ID_TOKEN, TokenUtil.createIdToken(clientDetails, userDetails, param.getNonce(), EndpointUtil.getIssuer(request)));
        }
        return response;
    }

    /**
     * RFC6749 4.4.  Client Credentials Grant
     *
     * @param param   request params
     * @param request current HTTP request
     * @return IhaServerResponse
     * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.4" target="_blank">4.4.  Client Credentials Grant</a>
     */
    public IhaServerResponse<String, Object> generateClientCredentialsResponse(IhaServerRequestParam param, HttpServletRequest request) {
        String clientId = param.getClientId();

        ClientDetails clientDetails = IhaServer.getContext().getClientDetailsService().getByClientId(clientId);
        String requestScope = param.getScope();

        OAuthUtil.validClientDetail(clientDetails);
        OAuthUtil.validateScope(requestScope, clientDetails.getScopes());
        OAuthUtil.validateGrantType(param.getGrantType(), clientDetails.getGrantTypes(), GrantType.CLIENT_CREDENTIALS);
        OAuthUtil.validateSecret(param, clientDetails, oAuth2Service);

        long expiresIn = OAuthUtil.getAccessTokenExpiresIn(clientDetails.getAccessTokenTimeToLive());

        AccessToken accessToken = TokenUtil.createClientCredentialsAccessToken(clientDetails, param.getGrantType(), requestScope, param.getNonce(), EndpointUtil.getIssuer(request));

        // https://tools.ietf.org/html/rfc6749#section-4.2.2
        // The authorization server MUST NOT issue a refresh token.
        IhaServerResponse<String, Object> response = new IhaServerResponse<String, Object>()
                .add(OAuth2ParameterNames.ACCESS_TOKEN, accessToken.getAccessToken())
                .add(OAuth2ParameterNames.EXPIRES_IN, expiresIn)
                .add(OAuth2ParameterNames.TOKEN_TYPE, IhaServerConstants.TOKEN_TYPE_BEARER);
        if (!StringUtil.isEmpty(requestScope)) {
            response.add(OAuth2ParameterNames.SCOPE, requestScope);
        }
        return response;
    }

    /**
     * RFC6749 6.  Refreshing an Access Token
     *
     * @param param   request params
     * @param request current HTTP request
     * @return IhaServerResponse
     * @see <a href="https://tools.ietf.org/html/rfc6749#section-6" target="_blank">6.  Refreshing an Access Token</a>
     */
    public IhaServerResponse<String, Object> generateRefreshTokenResponse(IhaServerRequestParam param, HttpServletRequest request) {
        TokenUtil.validateRefreshToken(param.getRefreshToken());

        try {
            AccessToken token = TokenUtil.getByRefreshToken(param.getRefreshToken());

            ClientDetails clientDetails = IhaServer.getContext().getClientDetailsService().getByClientId(token.getClientId());
            String requestScope = param.getScope();

            OAuthUtil.validClientDetail(clientDetails);
            OAuthUtil.validateScope(requestScope, clientDetails.getScopes());
            OAuthUtil.validateGrantType(param.getGrantType(), clientDetails.getGrantTypes(), GrantType.REFRESH_TOKEN);
            OAuthUtil.validateSecret(param, clientDetails, oAuth2Service);

            UserDetails userDetails = IhaServer.getContext().getUserDetailService().getById(token.getUserId());

            long expiresIn = OAuthUtil.getRefreshTokenExpiresIn(clientDetails.getRefreshTokenTimeToLive());

            AccessToken accessToken = TokenUtil.refreshAccessToken(userDetails, clientDetails, token, param.getNonce(), EndpointUtil.getIssuer(request));
            return new IhaServerResponse<String, Object>()
                    .add(OAuth2ParameterNames.ACCESS_TOKEN, accessToken.getAccessToken())
                    .add(OAuth2ParameterNames.REFRESH_TOKEN, accessToken.getRefreshToken())
                    .add(OAuth2ParameterNames.EXPIRES_IN, expiresIn)
                    .add(OAuth2ParameterNames.TOKEN_TYPE, IhaServerConstants.TOKEN_TYPE_BEARER)
                    .add(OAuth2ParameterNames.SCOPE, requestScope);
        } catch (Exception e) {
            throw new IhaServerException(ErrorResponse.SERVER_ERROR);
        }
    }
}
