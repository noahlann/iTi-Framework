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

package org.lan.iti.common.security.social.connect;

import lombok.AccessLevel;
import lombok.Getter;
import org.lan.iti.common.security.social.ServiceProvider;

/**
 * 构造服务提供者 {@link Connection}实例的工厂的基本抽象
 *
 * @author NorthLan
 * @date 2020-03-18
 * @url https://noahlan.com
 */
public abstract class ConnectionFactory<A> {

    @Getter
    private final String providerId;

    @Getter(AccessLevel.PROTECTED)
    private final ServiceProvider<A> serviceProvider;

    @Getter(AccessLevel.PROTECTED)
    private final ApiAdapter<A> apiAdapter;

    public ConnectionFactory(String providerId, ServiceProvider<A> serviceProvider, ApiAdapter<A> apiAdapter) {
        this.providerId = providerId;
        this.serviceProvider = serviceProvider;
        this.apiAdapter = nullSafeApiAdapter(apiAdapter);
    }

    /**
     * 创建 Connection
     *
     * @param data connection数据
     */
    public abstract Connection<A> createConnection(ConnectionData data);

    @SuppressWarnings("unchecked")
    private ApiAdapter<A> nullSafeApiAdapter(ApiAdapter<A> apiAdapter) {
        if (apiAdapter != null) {
            return apiAdapter;
        }
        return (ApiAdapter<A>) NullApiAdapter.INSTANCE;
    }
}
