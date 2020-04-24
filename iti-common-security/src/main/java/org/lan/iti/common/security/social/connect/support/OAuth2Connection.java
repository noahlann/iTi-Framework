/*
 *
 *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.common.security.social.connect.support;

import org.lan.iti.common.core.util.SystemClock;
import org.lan.iti.common.security.social.ServiceProvider;
import org.lan.iti.common.security.social.connect.ApiAdapter;
import org.lan.iti.common.security.social.connect.ConnectionData;
import org.lan.iti.common.security.social.connect.ConnectionValues;
import org.lan.iti.common.security.social.exception.ExpiredAuthorizationException;
import org.lan.iti.common.security.social.oauth2.OAuth2ServiceProvider;
import org.springframework.core.GenericTypeResolver;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 基于 oAuth 2.0 协议的连接
 *
 * @author NorthLan
 * @date 2020-03-30
 * @url https://noahlan.com
 */
public class OAuth2Connection<A> extends AbstractConnection<A> {
    private static final long serialVersionUID = -1404229087311914745L;

    private transient OAuth2ServiceProvider<A> serviceProvider;

    private String accessToken;
    private String refreshToken;
    private Long expireTime;
    //
    private String unionId;
    private transient A api;
    /**
     * api 动态代理, Api仅为接口的情况, 最终调用还是api方法
     */
    private transient A apiProxy;

    /**
     * 基于初次获取授权成功的数据创建OAuth2连接信息
     * 用于未创建过{@link org.lan.iti.common.security.social.connect.Connection}的情况下使用
     * <p>
     * providerUserId 可能为空,将会使用ServiceProvider提供的API解决此问题
     * </p>
     *
     * @param providerId      服务提供商ID
     * @param providerUserId  服务提供商对应当前连接的用户ID
     * @param accessToken     令牌
     * @param refreshToken    刷新令牌
     * @param expireTime      过期时间
     * @param serviceProvider 基于oAuth2的服务提供者
     * @param apiAdapter      以上服务提供者对应的ApiAdapter
     */
    public OAuth2Connection(String providerId, String providerUserId, String accessToken, String refreshToken, Long expireTime,
                            String unionId, OAuth2ServiceProvider<A> serviceProvider, String domain, ApiAdapter<A> apiAdapter) {
        super(domain, apiAdapter);
        this.serviceProvider = serviceProvider;
        initAccessToken(accessToken, refreshToken, expireTime, unionId);
        initApi();
        initApiProxy();
        initKey(providerId, providerUserId);
    }

    /**
     * 根据现有的连接信息创建新的连接对象
     * 用于已创建过{@link org.lan.iti.common.security.social.connect.Connection}的情况下使用
     * <p>
     * 此方式不会重建api调用,不会触发 {@link ApiAdapter#setConnectionValues(Object, ConnectionValues)}
     * </p>
     *
     * @param data            保此此连接状态的连接数据
     * @param serviceProvider 基于OAuth2的服务提供者
     * @param apiAdapter      以上服务提供者对应的ApiAdapter
     */
    public OAuth2Connection(ConnectionData data, OAuth2ServiceProvider<A> serviceProvider, ApiAdapter<A> apiAdapter) {
        super(data, apiAdapter);
        this.serviceProvider = serviceProvider;
        initAccessToken(data.getAccessToken(), data.getRefreshToken(), data.getExpireTime(), data.getUnionId());
        initApi();
        initApiProxy();
    }

    @Override
    public boolean hasExpired() {
        synchronized (getMonitor()) {
            return expireTime != null && SystemClock.now() >= expireTime;
        }
    }

    @Override
    public void refresh() {
        synchronized (getMonitor()) {
            // TODO serviceProvider 刷新 accessToken
            // initAccessToken
            initApi();
        }
    }

    @Override
    public A getApi() {
        if (apiProxy != null) {
            return apiProxy;
        } else {
            synchronized (getMonitor()) {
                return api;
            }
        }
    }

    @Override
    public ConnectionData createData() {
        synchronized (getMonitor()) {
            return new ConnectionData(null, getDomain(), getKey().getProviderId(), getKey().getProviderUserId(),
                    getDisplayName(), getProfileUrl(), getImageUrl(),
                    accessToken, null, refreshToken, expireTime, unionId);
        }
    }


    // region internal helpers
    private void initAccessToken(String accessToken, String refreshToken, Long expireTime, String unionId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expireTime = expireTime;
        this.unionId = unionId;
    }

    private void initApi() {
        this.api = serviceProvider.getApi(accessToken);
    }

    @SuppressWarnings("unchecked")
    private void initApiProxy() {
        Class<?> apiType = GenericTypeResolver.resolveTypeArgument(serviceProvider.getClass(), ServiceProvider.class);
        if (apiType != null && apiType.isInterface()) {
            apiProxy = (A) Proxy.newProxyInstance(apiType.getClassLoader(), new Class<?>[]{apiType}, new ApiInvocationHandler());
        }
    }

    /**
     * api执行代理器,代理所有api方法
     * <p>执行token超时判断逻辑</p>
     */
    private class ApiInvocationHandler implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            synchronized (getMonitor()) {
                if (hasExpired()) {
                    throw new ExpiredAuthorizationException(getKey().getProviderId());
                }
                try {
                    return method.invoke(OAuth2Connection.this.api, args);
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
            }
        }
    }
    // endregion
}
