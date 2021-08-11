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

package org.lan.iti.iha.social;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.config.AuthDefaultSource;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import org.lan.iti.iha.core.IhaUser;
import org.lan.iti.iha.core.cache.IhaCache;
import org.lan.iti.iha.core.config.AuthenticateConfig;
import org.lan.iti.iha.core.config.IhaConfig;
import org.lan.iti.iha.core.exception.IhaException;
import org.lan.iti.iha.core.exception.IhaSocialException;
import org.lan.iti.iha.core.exception.IhaUserException;
import org.lan.iti.iha.core.repository.IhaUserRepository;
import org.lan.iti.iha.core.result.IhaResponse;
import org.lan.iti.iha.core.result.IhaResponseCode;
import org.lan.iti.iha.core.strategy.AbstractIhaStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * automatically complete the authentication logic of third-party login through the policy class when logging in on the
 * social platform.
 * <p>
 * 1. Obtain and jump to the authorization link of the third-party system.
 * 2. Obtain the user information of the third party system through the callback parameters of the third party system.
 * 3. Save the user information of the third-party system to the developer's business system.
 * 4. Login is successful. Jump to the login success page.
 *
 * @author NorthLan
 * @date 2021-07-15
 * @url https://noahlan.com
 */
public class SocialStrategy extends AbstractIhaStrategy {
    private AuthStateCache authStateCache;

    public SocialStrategy(IhaUserRepository userRepository, IhaConfig ihaConfig) {
        super(userRepository, ihaConfig);
    }

    public SocialStrategy(IhaUserRepository userRepository, IhaConfig ihaConfig, IhaCache ihaCache) {
        super(userRepository, ihaConfig, ihaCache);
    }

    public SocialStrategy(IhaUserRepository userRepository, IhaConfig ihaConfig, AuthStateCache authStateCache) {
        super(userRepository, ihaConfig);
        this.authStateCache = authStateCache;
    }

    @Override
    public IhaResponse authenticate(AuthenticateConfig config, HttpServletRequest request, HttpServletResponse response) {
        IhaUser sessionUser = this.checkSession(request, response);
        if (null != sessionUser) {
            return IhaResponse.ok(sessionUser);
        }

        AuthRequest authRequest;
        try {
            authRequest = this.getAuthRequest(config);
        } catch (IhaException e) {
            return IhaResponse.error(e.getCode(), e.getMessage());
        }
        SocialConfig socialConfig = (SocialConfig) config;
        String source = socialConfig.getPlatform();

        AuthCallback authCallback = this.parseRequest(request);

        // If it is not a callback request, it must be a request to jump to the authorization link
        if (!this.isCallback(source, authCallback)) {
            String url = authRequest.authorize(socialConfig.getState());
            return IhaResponse.ok(url);
        }

        try {
            return this.login(request, response, source, authRequest, authCallback);
        } catch (IhaUserException e) {
            return IhaResponse.error(e.getCode(), e.getMessage());
        }
    }

    public IhaResponse refreshToken(AuthenticateConfig config, AuthToken authToken) {
        AuthRequest authRequest;
        try {
            authRequest = this.getAuthRequest(config);
        } catch (IhaException e) {
            return IhaResponse.error(e.getCode(), e.getMessage());
        }
        SocialConfig socialConfig = (SocialConfig) config;
        String source = socialConfig.getPlatform();

        AuthResponse<?> authUserAuthResponse;
        try {
            authUserAuthResponse = authRequest.refresh(authToken);
        } catch (Exception e) {
            throw new IhaSocialException("Third party refresh access token of `" + source + "` failed. " + e.getMessage());
        }
        if (!authUserAuthResponse.ok() || ObjectUtil.isNull(authUserAuthResponse.getData())) {
            throw new IhaUserException("Third party refresh access token of `" + source + "` failed. " + authUserAuthResponse.getMsg());
        }

        authToken = (AuthToken) authUserAuthResponse.getData();
        return IhaResponse.ok(authToken);
    }

    public IhaResponse revokeToken(AuthenticateConfig config, AuthToken authToken) {
        AuthRequest authRequest;
        try {
            authRequest = this.getAuthRequest(config);
        } catch (IhaException e) {
            return IhaResponse.error(e.getCode(), e.getMessage());
        }
        SocialConfig socialConfig = (SocialConfig) config;
        String source = socialConfig.getPlatform();

        AuthResponse<?> authUserAuthResponse;
        try {
            authUserAuthResponse = authRequest.revoke(authToken);
        } catch (Exception e) {
            throw new IhaSocialException("Third party refresh access token of `" + source + "` failed. " + e.getMessage());
        }
        if (!authUserAuthResponse.ok() || ObjectUtil.isNull(authUserAuthResponse.getData())) {
            throw new IhaUserException("Third party refresh access token of `" + source + "` failed. " + authUserAuthResponse.getMsg());
        }

        return IhaResponse.ok();
    }

    public IhaResponse getUserInfo(AuthenticateConfig config, AuthToken authToken) {
        AuthRequest authRequest;
        try {
            authRequest = this.getAuthRequest(config);
        } catch (IhaException e) {
            return IhaResponse.error(e.getCode(), e.getMessage());
        }
        SocialConfig socialConfig = (SocialConfig) config;
        String source = socialConfig.getPlatform();

        String funName = "getUserInfo";
        Method method;
        AuthUser res = null;
        IhaUserException IhaUserException = new IhaUserException("Failed to obtain user information on the third-party platform `" + source + "`");
        try {
            if ((method = ReflectUtil.getMethod(authRequest.getClass(), funName, AuthToken.class)) != null) {
                method.setAccessible(true);
                res = ReflectUtil.invoke(authRequest, method, authToken);
                if (null == res) {
                    throw IhaUserException;
                }
            }
        } catch (Exception e) {
            throw IhaUserException;
        }
        AuthUser authUser = res;
        return IhaResponse.ok(authUser);
    }

    private AuthRequest getAuthRequest(AuthenticateConfig config) {
        // Convert AuthenticateConfig to SocialConfig
        this.checkAuthenticateConfig(config, SocialConfig.class);
        SocialConfig socialConfig = (SocialConfig) config;
        String source = socialConfig.getPlatform();

        // Get the AuthConfig of JustAuth
        AuthConfig authConfig = socialConfig.getJustAuthConfig();
        if (ObjectUtil.isNull(authConfig)) {
            throw new IhaException(IhaResponseCode.MISS_AUTH_CONFIG);
        }

        // Instantiate the AuthRequest of JustAuth
        return JustAuthRequestContext.getRequest(source, socialConfig, authConfig, authStateCache);
    }

    /**
     * Login with third party authorization
     *
     * @param request      Third party callback request
     * @param response     current HTTP response
     * @param source       Third party platform name
     * @param authRequest  AuthRequest of justauth
     * @param authCallback Parse the parameters obtained by the third party callback request
     */
    private IhaResponse login(HttpServletRequest request, HttpServletResponse response, String source, AuthRequest authRequest, AuthCallback authCallback) throws IhaUserException {
        AuthResponse<?> authUserAuthResponse;
        try {
            authUserAuthResponse = authRequest.login(authCallback);
        } catch (Exception e) {
            throw new IhaSocialException("Third party login of `" + source + "` failed. " + e.getMessage());
        }
        if (!authUserAuthResponse.ok() || ObjectUtil.isNull(authUserAuthResponse.getData())) {
            throw new IhaUserException("Third party login of `" + source + "` cannot obtain user information. "
                    + authUserAuthResponse.getMsg());
        }
        AuthUser socialUser = (AuthUser) authUserAuthResponse.getData();
        IhaUser IhaUser = userRepository.getByPlatformAndUid(source, socialUser.getUuid());
        if (ObjectUtil.isNull(IhaUser)) {
            IhaUser = userRepository.createAndGetSocialUser(socialUser);
            if (ObjectUtil.isNull(IhaUser)) {
                throw new IhaUserException("Unable to save user information of " + source);
            }
        }

        return this.loginSuccess(IhaUser, request, response);
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

    /**
     * Parse the callback request and get the callback parameter object
     *
     * @param request Current callback request
     * @return AuthCallback
     */
    private AuthCallback parseRequest(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        if (MapUtil.isEmpty(params)) {
            return new AuthCallback();
        }
        JSONObject jsonObject = new JSONObject();
        params.forEach((key, val) -> {
            if (ObjectUtil.isNotNull(val)) {
                jsonObject.put(key, val[0]);
            }
        });
        return jsonObject.toJavaObject(AuthCallback.class);
    }
}
