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

package org.lan.iti.iha.social.security;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import me.zhyd.oauth.config.AuthDefaultSource;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import org.lan.iti.iha.oauth2.token.AccessToken;
import org.lan.iti.iha.oauth2.token.AccessTokenHelper;
import org.lan.iti.iha.security.IhaSecurity;
import org.lan.iti.iha.security.authentication.Authentication;
import org.lan.iti.iha.security.exception.authentication.AuthenticationException;
import org.lan.iti.iha.security.mgt.RequestParameter;
import org.lan.iti.iha.security.processor.ProcessChain;
import org.lan.iti.iha.security.userdetails.UserDetails;
import org.lan.iti.iha.security.userdetails.UserDetailsService;
import org.lan.iti.iha.social.SocialConfig;
import org.lan.iti.iha.social.exception.SocialAuthenticationException;
import org.lan.iti.iha.social.util.JustAuthUtil;

/**
 * @author NorthLan
 * @date 2021/8/16
 * @url https://blog.noahlan.com
 */
public class SocialAuthenticationProcessor extends AbstractSocialAuthenticationProcessor {
    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public boolean support(RequestParameter parameter, Authentication authentication) throws AuthenticationException {
        return parameter instanceof SocialRequestParameter && authentication instanceof SocialAuthenticationToken;
    }

    @Override
    public Authentication process(RequestParameter parameter, Authentication authentication, ProcessChain chain) throws AuthenticationException {
        SocialRequestParameter socialRequestParameter = (SocialRequestParameter) parameter;
        SocialConfig socialConfig = socialRequestParameter.getConfig();

        SocialAuthenticationToken token = (SocialAuthenticationToken) authentication;
        AuthRequest authRequest = token.getAuthRequest();
        AuthCallback authCallback = token.getAuthCallback();

        String source = socialConfig.getPlatform();
        // If it is not a callback request, it must be a request to jump to the authorization link
        if (!this.isCallback(source, authCallback)) {
            return token.setRedirectUrl(authRequest.authorize(socialConfig.getState()));
        }
        //
        AuthResponse<?> authUserAuthResponse;
        try {
            authUserAuthResponse = authRequest.login(authCallback);
        } catch (Exception e) {
            throw new SocialAuthenticationException("Third party login of `" + source + "` failed. " + e.getMessage());
        }
        if (!authUserAuthResponse.ok() || ObjectUtil.isNull(authUserAuthResponse.getData())) {
            throw new SocialAuthenticationException("Third party login of `" + source + "` cannot obtain user information. "
                    + authUserAuthResponse.getMsg());
        }
        AuthUser socialUser = (AuthUser) authUserAuthResponse.getData();
        // socialUser -> 转换为 AccessToken
        String tokenJson = JustAuthUtil.toJsonString(socialUser.getToken());
        AccessToken accessToken = AccessTokenHelper.toAccessToken(tokenJson);

        UserDetailsService userDetailsService = IhaSecurity.getContext().getUserDetailsService();

        UserDetails userDetails = userDetailsService.fromToken(source, accessToken, socialUser.getRawUserInfo().getInnerMap());
        if (userDetails == null) {
            throw new AuthenticationException("failed to load UserDetails from token");
        }
        return chain.process(socialRequestParameter, new SocialAuthenticationToken(accessToken, userDetails, userDetails.getAuthorities()));
    }

    /**
     * Whether it is the callback request after the authorization of the third-party platform is completed,
     * the judgment basis is as follows:
     * - Code is not empty
     *
     * @param source       Third party platform name
     * @param authCallback Parameters resolved by callback request
     * @return When true is returned, the current HTTP request is a callback request
     */
    private boolean isCallback(String source, AuthCallback authCallback) {
        if (source.equals(AuthDefaultSource.TWITTER.name()) && ObjectUtil.isNotNull(authCallback.getOauth_token())) {
            return true;
        }
        String code = authCallback.getCode();
        if (source.equals(AuthDefaultSource.ALIPAY.name())) {
            code = authCallback.getAuth_code();
        } else if (source.equals(AuthDefaultSource.HUAWEI.name())) {
            code = authCallback.getAuthorization_code();
        }
        return !StrUtil.isEmpty(code);
    }

//    public IhaResponse refreshToken(AuthenticateConfig config, AuthToken authToken) {
//        AuthRequest authRequest;
//        try {
//            authRequest = this.getAuthRequest(config);
//        } catch (IhaException e) {
//            return IhaResponse.error(e.getCode(), e.getMessage());
//        }
//        SocialConfig socialConfig = (SocialConfig) config;
//        String source = socialConfig.getPlatform();
//
//        AuthResponse<?> authUserAuthResponse;
//        try {
//            authUserAuthResponse = authRequest.refresh(authToken);
//        } catch (Exception e) {
//            throw new IhaSocialException("Third party refresh access token of `" + source + "` failed. " + e.getMessage());
//        }
//        if (!authUserAuthResponse.ok() || ObjectUtil.isNull(authUserAuthResponse.getData())) {
//            throw new IhaUserException("Third party refresh access token of `" + source + "` failed. " + authUserAuthResponse.getMsg());
//        }
//
//        authToken = (AuthToken) authUserAuthResponse.getData();
//        return IhaResponse.ok(authToken);
//    }
//
//    public IhaResponse revokeToken(AuthenticateConfig config, AuthToken authToken) {
//        AuthRequest authRequest;
//        try {
//            authRequest = this.getAuthRequest(config);
//        } catch (IhaException e) {
//            return IhaResponse.error(e.getCode(), e.getMessage());
//        }
//        SocialConfig socialConfig = (SocialConfig) config;
//        String source = socialConfig.getPlatform();
//
//        AuthResponse<?> authUserAuthResponse;
//        try {
//            authUserAuthResponse = authRequest.revoke(authToken);
//        } catch (Exception e) {
//            throw new IhaSocialException("Third party refresh access token of `" + source + "` failed. " + e.getMessage());
//        }
//        if (!authUserAuthResponse.ok() || ObjectUtil.isNull(authUserAuthResponse.getData())) {
//            throw new IhaUserException("Third party refresh access token of `" + source + "` failed. " + authUserAuthResponse.getMsg());
//        }
//
//        return IhaResponse.ok();
//    }
//
//    public IhaResponse getUserInfo(AuthenticateConfig config, AuthToken authToken) {
//        AuthRequest authRequest;
//        try {
//            authRequest = this.getAuthRequest(config);
//        } catch (IhaException e) {
//            return IhaResponse.error(e.getCode(), e.getMessage());
//        }
//        SocialConfig socialConfig = (SocialConfig) config;
//        String source = socialConfig.getPlatform();
//
//        String funName = "getUserInfo";
//        Method method;
//        AuthUser res = null;
//        IhaUserException IhaUserException = new IhaUserException("Failed to obtain user information on the third-party platform `" + source + "`");
//        try {
//            if ((method = ReflectUtil.getMethod(authRequest.getClass(), funName, AuthToken.class)) != null) {
//                method.setAccessible(true);
//                res = ReflectUtil.invoke(authRequest, method, authToken);
//                if (null == res) {
//                    throw IhaUserException;
//                }
//            }
//        } catch (Exception e) {
//            throw IhaUserException;
//        }
//        AuthUser authUser = res;
//        return IhaResponse.ok(authUser);
//    }
}
