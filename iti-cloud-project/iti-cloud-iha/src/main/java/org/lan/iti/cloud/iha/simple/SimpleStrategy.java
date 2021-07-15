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

package org.lan.iti.cloud.iha.simple;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import lombok.val;
import org.lan.iti.cloud.iha.core.IhaUser;
import org.lan.iti.cloud.iha.core.cache.IhaCache;
import org.lan.iti.cloud.iha.core.config.AuthenticateConfig;
import org.lan.iti.cloud.iha.core.config.IhaConfig;
import org.lan.iti.cloud.iha.core.exception.IhaException;
import org.lan.iti.cloud.iha.core.repository.IhaUserRepository;
import org.lan.iti.cloud.iha.core.result.IhaErrorCode;
import org.lan.iti.cloud.iha.core.result.IhaResponse;
import org.lan.iti.cloud.iha.core.strategy.AbstractIhaStrategy;
import org.lan.iti.cloud.iha.core.util.RequestUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author NorthLan
 * @date 2021-07-07
 * @url https://noahlan.com
 */
public class SimpleStrategy extends AbstractIhaStrategy {
    public SimpleStrategy(IhaUserRepository userRepository, IhaConfig ihaConfig) {
        super(userRepository, ihaConfig);
    }

    public SimpleStrategy(IhaUserRepository userRepository, IhaConfig ihaConfig, IhaCache ihaCache) {
        super(userRepository, ihaConfig, ihaCache);
    }

    @Override
    public IhaResponse authenticate(AuthenticateConfig config, HttpServletRequest request, HttpServletResponse response) {
        // Convert AuthenticateConfig to SimpleConfig
        try {
            this.checkAuthenticateConfig(config, SimpleConfig.class);
        } catch (IhaException e) {
            return IhaResponse.error(e.getCode(), e.getMessage());
        }
        SimpleConfig simpleConfig = (SimpleConfig) config;

        IhaUser sessionUser;
        try {
            sessionUser = this.checkSessionAndCookie(simpleConfig, request, response);
        } catch (IhaException e) {
            return IhaResponse.error(e.getCode(), e.getMessage());
        }
        if (null != sessionUser) {
            return IhaResponse.success(sessionUser);
        }

        SimpleCredentials credentials = this.doResolveCredential(request, simpleConfig);
        if (null == credentials) {
            return IhaResponse.error(IhaErrorCode.MISS_CREDENTIALS);
        }
        IhaUser user = userRepository.getByCredentials(credentials, simpleConfig);
        if (null == user) {
            return IhaResponse.error(IhaErrorCode.NOT_EXIST_USER);
        }

        boolean valid = userRepository.validPassword(credentials.getPassword(), user);
        if (!valid) {
            return IhaResponse.error(IhaErrorCode.INVALID_PASSWORD);
        }

        return this.loginSuccess(simpleConfig, credentials, user, request, response);
    }

    /**
     * login successful
     *
     * @param simpleConfig Authenticate Config
     * @param credentials  Username password credential
     * @param user         IhaUser
     * @param request      The request to authenticate
     * @param response     The response to authenticate
     */
    private IhaResponse loginSuccess(SimpleConfig simpleConfig, SimpleCredentials credentials, IhaUser user, HttpServletRequest request, HttpServletResponse response) {
        user.setRememberMe(credentials.isRememberMe());
        if (credentials.isRememberMe()) {
            String cookieDomain = ObjectUtil.isNotEmpty(simpleConfig.getRememberMeCookieDomain()) ? simpleConfig.getRememberMeCookieDomain() : null;
            // add cookie
            RequestUtil.setCookie(response,
                    simpleConfig.getRememberMeCookieKey(),
                    this.encodeCookieValue(user, simpleConfig),
                    Convert.toInt(simpleConfig.getRememberMeCookieMaxAge()),
                    "/",
                    cookieDomain
            );
        }
        return this.loginSuccess(user, request, response);
    }

    /**
     * check session and cookie
     *
     * @param simpleConfig Authenticate Config
     * @param request      The request to authenticate
     * @param response     The response to authenticate
     * @return true to login success, false to login
     */
    private IhaUser checkSessionAndCookie(SimpleConfig simpleConfig, HttpServletRequest request, HttpServletResponse response) throws IhaException {
        IhaUser sessionUser = this.checkSession(request, response);
        if (null != sessionUser) {
            return sessionUser;
        }
        if (!RememberMeUtils.enableRememberMe(request, simpleConfig)) {
            return null;
        }

        Cookie cookie = RequestUtil.getCookie(request, simpleConfig.getRememberMeCookieKey());
        if (ObjectUtil.isNull(cookie)) {
            return null;
        }

        SimpleCredentials credential = this.decodeCookieValue(simpleConfig, cookie.getValue());
        if (ObjectUtil.isNull(credential)) {
            return null;
        }

        IhaUser user = userRepository.getByCredentials(credential, simpleConfig);
        if (null == user) {
            return null;
        }
        // redirect login successful
        this.loginSuccess(user, request, response);
        return user;
    }

    /**
     * decode Username password credential
     *
     * @param simpleConfig Authenticate Config
     * @param cookieValue  Cookie value
     * @return Username password credential
     */
    private SimpleCredentials decodeCookieValue(SimpleConfig simpleConfig, String cookieValue) throws IhaException {
        RememberMeDetails details = RememberMeUtils.decode(simpleConfig, cookieValue);
        if (ObjectUtil.isNotNull(details)) {
            // return no longer password and remember me
            return SimpleCredentials.builder()
                    .principal(details.getPrincipal())
                    .build();
        }
        return null;
    }

    /**
     * The value of the encrypted cookie
     *
     * @param user         Iha user
     * @param simpleConfig Authenticate Config
     * @return Encode cookie value string
     */
    private String encodeCookieValue(IhaUser user, SimpleConfig simpleConfig) {
        return RememberMeUtils.encode(simpleConfig, user.getUsername()).getEncoded();
    }

    /**
     * @param request      The request to authenticate
     * @param simpleConfig Authenticate Config
     * @return Username password credential
     */
    private SimpleCredentials doResolveCredential(HttpServletRequest request, SimpleConfig simpleConfig) {
        val map = ServletUtil.getParamMap(request);
        String principal = map.get(simpleConfig.getPrincipalField());
        String password = map.get(simpleConfig.getPasswordField());
        String type = map.get(simpleConfig.getTypeField());
        if (null == principal) {
            return null;
        }
        String code = map.get(simpleConfig.getCodeField());
        String rememberMe = map.get(simpleConfig.getRememberMeField());

        MapUtil.removeAny(map,
                simpleConfig.getPrincipalField(),
                simpleConfig.getPasswordField(),
                simpleConfig.getCodeField(),
                simpleConfig.getTypeField(),
                simpleConfig.getRememberMeField());

        return SimpleCredentials.builder()
                .principal(principal)
                .password(password)
                .code(code)
                .type(type)
                .rememberMe(BooleanUtil.toBoolean(rememberMe))
                .extra(map)
                .build();
    }
}
