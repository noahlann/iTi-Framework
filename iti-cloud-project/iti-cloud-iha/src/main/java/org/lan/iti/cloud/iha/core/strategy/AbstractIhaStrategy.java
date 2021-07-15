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
package org.lan.iti.cloud.iha.core.strategy;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import org.lan.iti.cloud.iha.core.IhaUser;
import org.lan.iti.cloud.iha.core.cache.IhaCache;
import org.lan.iti.cloud.iha.core.cache.IhaCacheConfig;
import org.lan.iti.cloud.iha.core.cache.IhaLocalCache;
import org.lan.iti.cloud.iha.core.config.AuthenticateConfig;
import org.lan.iti.cloud.iha.core.config.IhaConfig;
import org.lan.iti.cloud.iha.core.context.IhaAuthentication;
import org.lan.iti.cloud.iha.core.context.IhaContext;
import org.lan.iti.cloud.iha.core.exception.IhaException;
import org.lan.iti.cloud.iha.core.repository.IhaUserRepository;
import org.lan.iti.cloud.iha.core.result.IhaErrorCode;
import org.lan.iti.cloud.iha.core.result.IhaResponse;
import org.lan.iti.cloud.iha.core.store.IhaUserStore;
import org.lan.iti.cloud.iha.core.store.SessionIhaUserStore;
import org.lan.iti.cloud.iha.core.store.SsoIhaUserStore;
import org.lan.iti.cloud.iha.sso.IhaSsoHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * General policy handling methods and parameters, policies of other platforms can inherit
 * {@code AbstractIhaStrategy}, and override the constructor
 *
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
public abstract class AbstractIhaStrategy implements IhaStrategy {

    /**
     * Abstract the user-related function interface, which is implemented by the caller business system.
     */
    protected IhaUserRepository userRepository;

    /**
     * Iha configuration.
     */
    protected IhaContext context;

    /**
     * `Strategy` constructor.
     *
     * @param userRepository ihaUserRepository
     * @param ihaConfig      ihaConfig
     */
    public AbstractIhaStrategy(IhaUserRepository userRepository, IhaConfig ihaConfig) {
        this(userRepository, ihaConfig, new IhaLocalCache());
    }

    /**
     * `Strategy` constructor.
     *
     * @param userRepository userRepository
     * @param ihaCache       cache
     * @param ihaConfig      ihaConfig
     */
    public AbstractIhaStrategy(IhaUserRepository userRepository, IhaConfig ihaConfig, IhaCache ihaCache) {
        this(userRepository, ihaConfig, ihaConfig.isSso() ? new SsoIhaUserStore(userRepository, ihaConfig.getSsoConfig()) : new SessionIhaUserStore(), ihaCache);
    }

    /**
     * `Strategy` constructor.
     *
     * @param userRepository userRepository
     * @param ihaConfig      ihaConfig
     * @param ihaUserStore   IhaUserStore
     * @param ihaCache       Iha cache
     */
    public AbstractIhaStrategy(IhaUserRepository userRepository, IhaConfig ihaConfig, IhaUserStore ihaUserStore, IhaCache ihaCache) {
        this.userRepository = userRepository;
        if (ihaConfig.isSso()) {
            // init Kisso config
            IhaSsoHelper.initKissoConfig(ihaConfig.getSsoConfig());
        }
        this.context = new IhaContext(ihaUserStore, ihaCache, ihaConfig);

        IhaAuthentication.setContext(this.context);

        // Update the cache validity period
        IhaCacheConfig.timeout = ihaConfig.getCacheExpireTime();
    }

    /**
     * Verify whether the user logs in. If so, jump to {@code ihaConfig.getSuccessRedirect()}. Otherwise, return {@code false}
     *
     * @param request  Current IhaAuthentication Request
     * @param response current HTTP response
     * @return boolean
     */
    protected IhaUser checkSession(HttpServletRequest request, HttpServletResponse response) {
        return context.getUserStore().get(request, response);
    }

    protected IhaResponse loginSuccess(IhaUser ihaUser, HttpServletRequest request, HttpServletResponse response) {
        context.getUserStore().save(request, response, ihaUser);
        return IhaResponse.success(ihaUser);
    }

    /**
     * Verify that the AuthenticateConfig is of the specified class type
     *
     * @param sourceConfig      The parameters passed in by the caller
     * @param targetConfigClazz The actual parameter class type
     */
    protected void checkAuthenticateConfig(AuthenticateConfig sourceConfig, Class<?> targetConfigClazz) throws IhaException {
        if (ObjectUtil.isNull(sourceConfig)) {
            throw new IhaException(IhaErrorCode.MISS_AUTHENTICATE_CONFIG);
        }
        if (!ClassUtil.isAssignable(sourceConfig.getClass(), targetConfigClazz)) {
            throw new IhaException("Unsupported parameter type, please use " + ClassUtil.getClassName(targetConfigClazz, true) + ", a subclass of AuthenticateConfig");
        }
    }
}
