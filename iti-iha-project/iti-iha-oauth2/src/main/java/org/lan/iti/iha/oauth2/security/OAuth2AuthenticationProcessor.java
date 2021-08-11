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

package org.lan.iti.iha.oauth2.security;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.xkcoding.json.util.Kv;
import org.lan.iti.common.extension.ExtensionLoader;
import org.lan.iti.iha.core.exception.IhaOAuth2Exception;
import org.lan.iti.iha.oauth2.*;
import org.lan.iti.iha.oauth2.pkce.PkceHelper;
import org.lan.iti.iha.oauth2.token.AccessToken;
import org.lan.iti.iha.oauth2.token.AccessTokenProvider;
import org.lan.iti.iha.oauth2.util.OAuth2Util;
import org.lan.iti.iha.security.IhaSecurity;
import org.lan.iti.iha.security.authentication.Authentication;
import org.lan.iti.iha.security.exception.AuthenticationException;
import org.lan.iti.iha.security.mgt.RequestParameter;
import org.lan.iti.iha.security.processor.ProcessChain;
import org.lan.iti.iha.security.userdetails.UserDetails;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth2 登录
 *
 * @author NorthLan
 * @date 2021/8/3
 * @url https://blog.noahlan.com
 */
public class OAuth2AuthenticationProcessor extends AbstractOAuth2AuthenticationProcessor {

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public boolean support(RequestParameter parameter, Authentication authentication) {
        return parameter instanceof OAuth2RequestParameter &&
                authentication instanceof OAuth2AuthenticationToken;
    }

    @Override
    public Authentication process(RequestParameter parameter, Authentication authentication, ProcessChain chain) throws AuthenticationException {
        OAuth2RequestParameter oAuth2RequestParameter = (OAuth2RequestParameter) parameter;
        OAuth2Config oAuth2Config = oAuth2RequestParameter.getConfig();

        boolean isPasswordOrClientFlow = oAuth2Config.getGrantType() == GrantType.PASSWORD ||
                oAuth2Config.getGrantType() == GrantType.CLIENT_CREDENTIALS;

        // If it is not a callback request, it must be a request to jump to the authorization link
        // If it is a password authorization request or a client authorization request, the token will be obtained directly
        if (!OAuth2Util.isCallback(oAuth2RequestParameter, oAuth2Config) && !isPasswordOrClientFlow) {
            return chain.process(parameter, new OAuth2AuthenticationToken(getAuthorizationUrl(oAuth2Config)));
        }

        AccessToken accessToken = getTokenProvider(oAuth2Config).getToken(oAuth2RequestParameter, oAuth2Config);
        UserDetails userDetails = getUserInfo(oAuth2Config, accessToken);
        return chain.process(parameter, new OAuth2AuthenticationToken(accessToken, userDetails, userDetails.getAuthorities()));
    }

    private UserDetails getUserInfo(OAuth2Config oAuthConfig, AccessToken accessToken) throws IhaOAuth2Exception {
        Map<String, String> params = new HashMap<>(1);
        params.put(OAuth2ParameterNames.ACCESS_TOKEN, accessToken.getAccessToken());

        Kv userInfo = OAuth2Util.request(oAuthConfig.getUserInfoEndpointMethodType(), oAuthConfig.getUserinfoUrl(), params);

        OAuth2Util.checkOauthResponse(userInfo, "OAuth2Strategy failed to get userInfo with accessToken.");

        return IhaSecurity.getContext().getUserDetailsService().fromToken(oAuthConfig.getPlatform(), accessToken, userInfo);
    }

    private AccessTokenProvider getTokenProvider(OAuth2Config config) {
        AccessTokenProvider provider;
        ExtensionLoader<AccessTokenProvider, String> loader = ExtensionLoader.getLoader(AccessTokenProvider.class);
        provider = loader.getFirst(config.getResponseType());
        if (provider == null) {
            provider = loader.getFirst(config.getGrantType().getType());
        }
        if (provider == null) {
            throw new AuthenticationException("failed to get AccessToken. Missing required parameters");
        }
        return provider;
    }

    public String getAuthorizationUrl(OAuth2Config oAuthConfig) {
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
    private String generateAuthorizationCodeGrantUrl(OAuth2Config oAuthConfig) {
        Map<String, Object> params = new HashMap<>(7);
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
        // autoapprove
        params.put(OAuth2ParameterNames.AUTOAPPROVE, oAuthConfig.isAutoapprove() ? Boolean.TRUE.toString() : Boolean.FALSE.toString());
        params.put(OAuth2ParameterNames.STATE, oAuthConfig.getState());

        IhaSecurity.getContext().getCache().set(OAuth2Constants.STATE_CACHE_KEY.concat(oAuthConfig.getClientId()), state);
        // Pkce is only applicable to authorization code mode
        if (StrUtil.equals(oAuthConfig.getResponseType(), OAuth2ResponseType.CODE) && oAuthConfig.isRequireProofKey()) {
            params.putAll(PkceHelper.generatePkceParameters(oAuthConfig));
        }
        String query = URLUtil.buildQuery(params, StandardCharsets.UTF_8);
        return oAuthConfig.getAuthorizationUrl().concat("?").concat(query);
    }
}
