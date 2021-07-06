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

package org.lan.iti.cloud.iha.oauth2;

import cn.hutool.core.util.*;
import com.xkcoding.json.util.Kv;
import org.lan.iti.cloud.iha.core.IhaUser;
import org.lan.iti.cloud.iha.core.cache.IhaCache;
import org.lan.iti.cloud.iha.core.config.AuthenticateConfig;
import org.lan.iti.cloud.iha.core.config.IhaConfig;
import org.lan.iti.cloud.iha.core.context.IhaAuthentication;
import org.lan.iti.cloud.iha.core.exception.IhaException;
import org.lan.iti.cloud.iha.core.exception.IhaOAuth2Exception;
import org.lan.iti.cloud.iha.core.repository.IhaUserRepository;
import org.lan.iti.cloud.iha.core.result.IhaErrorCode;
import org.lan.iti.cloud.iha.core.result.IhaResponse;
import org.lan.iti.cloud.iha.core.strategy.AbstractIhaStrategy;
import org.lan.iti.cloud.iha.oauth2.pkce.PkceHelper;
import org.lan.iti.cloud.iha.oauth2.token.AccessToken;
import org.lan.iti.cloud.iha.oauth2.token.AccessTokenHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
public class OAuth2Strategy extends AbstractIhaStrategy {
    public OAuth2Strategy(IhaUserRepository userRepository, IhaConfig ihaConfig) {
        super(userRepository, ihaConfig);
    }

    public OAuth2Strategy(IhaUserRepository userRepository, IhaConfig ihaConfig, IhaCache ihaCache) {
        super(userRepository, ihaConfig, ihaCache);
    }

    @Override
    public IhaResponse authenticate(AuthenticateConfig config, HttpServletRequest request, HttpServletResponse response) {
        try {
            OAuth2Util.checkOauthCallbackRequest(request.getParameter("error"), request.getParameter("error_description"),
                    "Oauth2strategy request failed.");
        } catch (IhaOAuth2Exception e) {
            return IhaResponse.error(e.getCode(), e.getMessage());
        }

        IhaUser sessionUser = this.checkSession(request, response);
        if (null != sessionUser) {
            return IhaResponse.success(sessionUser);
        }

        try {
            this.checkAuthenticateConfig(config, OAuthConfig.class);
        } catch (IhaException e) {
            return IhaResponse.error(e.getCode(), e.getMessage());
        }
        OAuthConfig oAuthConfig = (OAuthConfig) config;

        try {
            OAuth2Util.checkOauthConfig(oAuthConfig);
        } catch (IhaOAuth2Exception e) {
            return IhaResponse.error(e.getCode(), e.getMessage());
        }

        boolean isPasswordOrClientMode = oAuthConfig.getGrantType() == GrantType.PASSWORD ||
                oAuthConfig.getGrantType() == GrantType.CLIENT_CREDENTIALS;

        // If it is not a callback request, it must be a request to jump to the authorization link
        // If it is a password authorization request or a client authorization request, the token will be obtained directly
        if (!OAuth2Util.isCallback(request, oAuthConfig) && !isPasswordOrClientMode) {
            String authorizationUrl = getAuthorizationUrl(oAuthConfig);
            return IhaResponse.success(authorizationUrl);
        } else {
            AccessToken accessToken;
            try {
                accessToken = AccessTokenHelper.getToken(request, oAuthConfig);
            } catch (IhaOAuth2Exception e) {
                return IhaResponse.error(e.getCode(), e.getMessage());
            }
            IhaUser user;
            try {
                user = getUserInfo(oAuthConfig, accessToken);
            } catch (IhaOAuth2Exception e) {
                return IhaResponse.error(e.getCode(), e.getMessage());
            }

            if (null == user) {
                return IhaResponse.error(IhaErrorCode.UNABLE_SAVE_USERINFO);
            }
            return this.loginSuccess(user, request, response);
        }
    }

    private IhaUser getUserInfo(OAuthConfig oAuthConfig, AccessToken accessToken) throws IhaOAuth2Exception {
        Map<String, String> params = new HashMap<>(6);
        params.put(OAuth2ParameterNames.ACCESS_TOKEN, accessToken.getAccessToken());

        Kv userInfo = OAuth2Util.request(oAuthConfig.getUserInfoEndpointMethodType(), oAuthConfig.getUserinfoUrl(), params);

        OAuth2Util.checkOauthResponse(userInfo, "OAuth2Strategy failed to get userInfo with accessToken.");

        IhaUser user = this.userRepository.createAndGetOauth2User(oAuthConfig.getPlatform(), userInfo, accessToken);
        if (ObjectUtil.isNull(user)) {
            return null;
        }
        return user;
    }

    private String getAuthorizationUrl(OAuthConfig oAuthConfig) {
        String url = null;
        // 4.1.  Authorization Code Grant https://tools.ietf.org/html/rfc6749#section-4.1
        // 4.2.  Implicit Grant https://tools.ietf.org/html/rfc6749#section-4.2
        if (StrUtil.equalsAny(oAuthConfig.getResponseType(), OAuth2ResponseType.CODE, OAuth2ResponseType.TOKEN)) {
            url = generateAuthorizationCodeGrantUrl(oAuthConfig);
        }
        return url;
    }

    /**
     * It is suitable for authorization code mode(rfc6749#4.1) and implicit authorization mode(rfc6749#4.2).
     * When it is in authorization code mode, the callback requests return code and state;
     * when it is in implicit authorization mode, the callback requests return token related data
     *
     * @param oAuthConfig oauth config
     * @return authorize request url
     * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.1" target="_blank">4.1.  Authorization Code Grant</a>
     * @see <a href="https://tools.ietf.org/html/rfc6749#section-4.2" target="_blank">4.2.  Implicit Grant</a>
     */
    private String generateAuthorizationCodeGrantUrl(OAuthConfig oAuthConfig) {
        Map<String, Object> params = new HashMap<>(6);
        params.put(OAuth2ParameterNames.RESPONSE_TYPE, oAuthConfig.getResponseType());
        params.put(OAuth2ParameterNames.CLIENT_ID, oAuthConfig.getClientId());
        if (StrUtil.isNotBlank(oAuthConfig.getCallbackUrl())) {
            params.put(OAuth2ParameterNames.REDIRECT_URI, oAuthConfig.getCallbackUrl());
        }
        if (ArrayUtil.isNotEmpty(oAuthConfig.getScopes())) {
            params.put(OAuth2ParameterNames.SCOPE, String.join(OAuth2Constants.SCOPE_SEPARATOR, oAuthConfig.getScopes()));
        }
        String state = oAuthConfig.getState();
        if (StrUtil.isBlank(state)) {
            state = RandomUtil.randomString(6);
        }
        params.put(OAuth2ParameterNames.STATE, oAuthConfig.getState());
        IhaAuthentication.getContext().getCache().set(OAuth2Constants.STATE_CACHE_KEY.concat(oAuthConfig.getClientId()), state);
        // Pkce is only applicable to authorization code mode
        if (StrUtil.equals(oAuthConfig.getResponseType(), OAuth2ResponseType.CODE) && oAuthConfig.isRequireProofKey()) {
            params.putAll(PkceHelper.generatePkceParameters(oAuthConfig));
        }
        String query = URLUtil.buildQuery(params, StandardCharsets.UTF_8);
        return oAuthConfig.getAuthorizationUrl().concat("?").concat(query);
    }
}
