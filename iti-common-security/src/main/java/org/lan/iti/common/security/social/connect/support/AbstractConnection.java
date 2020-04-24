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

import org.lan.iti.common.security.social.connect.*;

/**
 * Connection实现 抽象类
 *
 * @author NorthLan
 * @date 2020-03-30
 * @url https://noahlan.com
 */
public abstract class AbstractConnection<A> implements Connection<A> {
    private static final long serialVersionUID = 3924316133659544895L;

    private transient final ApiAdapter<A> apiAdapter;
    private transient final Object monitor = new Object();

    private ConnectionKey key;
    private String displayName;
    private String profileUrl;
    private String imageUrl;
    private String domain; // 域

    private boolean valuesInitialized;

    /**
     * 新的连接
     *
     * @param apiAdapter api适配器
     */
    public AbstractConnection(String domain, ApiAdapter<A> apiAdapter) {
        this.domain = domain;
        this.apiAdapter = apiAdapter;
    }

    /**
     * 从已有数据创建
     *
     * @param data       数据
     * @param apiAdapter api适配器
     */
    public AbstractConnection(ConnectionData data, ApiAdapter<A> apiAdapter) {
        this.key = new ConnectionKey(data.getProviderId(), data.getProviderUserId());
        this.apiAdapter = apiAdapter;
        this.displayName = data.getDisplayName();
        this.profileUrl = data.getProfileUrl();
        this.imageUrl = data.getImageUrl();
        this.domain = data.getDomain();
        this.valuesInitialized = true;
    }

    @Override
    public ConnectionKey getKey() {
        return key;
    }

    @Override
    public String getDisplayName() {
        synchronized (monitor) {
            initValues();
            return displayName;
        }
    }

    @Override
    public String getProfileUrl() {
        synchronized (monitor) {
            initValues();
            return profileUrl;
        }
    }

    @Override
    public String getImageUrl() {
        synchronized (monitor) {
            initValues();
            return imageUrl;
        }
    }

    @Override
    public void sync() {
        synchronized (monitor) {
            initValues();
        }
    }

    @Override
    public String getDomain() {
        return this.domain;
    }

    @Override
    public boolean test() {
        return apiAdapter.test(getApi());
    }

    @Override
    public boolean hasExpired() {
        return false;
    }

    @Override
    public void refresh() {
    }

    @Override
    public void updateStatus(String message) {
        apiAdapter.updateStatus(getApi(), message);
    }

    @Override
    public abstract A getApi();

    @Override
    public abstract ConnectionData createData();

    // region subclass

    /**
     * 提供子类调用初始化provider信息的接口，每当新建连接时需要调用
     * {@link ApiAdapter#setConnectionValues(Object, ConnectionValues)}
     *
     * @param providerId     提供商id
     * @param providerUserId 提供商用户id
     */
    protected void initKey(String providerId, String providerUserId) {
        if (providerUserId == null) {
            providerUserId = setValues().providerUserId;
        }
        key = new ConnectionKey(providerId, providerUserId);
    }


    /**
     * 为子类提供monitor模拟访问，用于同步锁
     * <code>
     * synchronized(getMonitor()){
     * ...sync access
     * }
     * </code>
     */
    protected Object getMonitor() {
        return this.monitor;
    }
    // endregion

    // region internal helpers
    private void initValues() {
        if (!valuesInitialized) {
            setValues();
        }
    }

    private ServiceProviderConnectionValuesImpl setValues() {
        ServiceProviderConnectionValuesImpl values = new ServiceProviderConnectionValuesImpl();
        apiAdapter.setConnectionValues(getApi(), values);
        this.valuesInitialized = true;
        return values;
    }

    private class ServiceProviderConnectionValuesImpl implements ConnectionValues {
        private String providerUserId;

        @Override
        public void setProviderUserId(String providerUserId) {
            this.providerUserId = providerUserId;
        }

        @Override
        public void setDisplayName(String displayName) {
            AbstractConnection.this.displayName = displayName;
        }

        @Override
        public void setProfileUrl(String profileUrl) {
            AbstractConnection.this.profileUrl = profileUrl;
        }

        @Override
        public void setImageUrl(String imageUrl) {
            AbstractConnection.this.imageUrl = imageUrl;
        }
    }
    // endregion
}
