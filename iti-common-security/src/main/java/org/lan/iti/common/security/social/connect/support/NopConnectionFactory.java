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

import org.lan.iti.common.security.social.ServiceProvider;
import org.lan.iti.common.security.social.connect.ApiAdapter;
import org.lan.iti.common.security.social.connect.Connection;
import org.lan.iti.common.security.social.connect.ConnectionData;
import org.lan.iti.common.security.social.connect.ConnectionFactory;
import org.lan.iti.common.security.social.nop.NopGrant;
import org.lan.iti.common.security.social.nop.NopServiceProvider;

/**
 * Nop连接工厂
 *
 * @author NorthLan
 * @date 2020-04-02
 * @url https://noahlan.com
 */
public class NopConnectionFactory<S> extends ConnectionFactory<S> {

    public NopConnectionFactory(String providerId, ServiceProvider<S> serviceProvider, ApiAdapter<S> apiAdapter) {
        super(providerId, serviceProvider, apiAdapter);
    }

    @Override
    public Connection<S> createConnection(ConnectionData data) {
        return new NopConnection<>(data, getNopServiceProvider(), getApiAdapter());
    }

    public Connection<S> createConnection(NopGrant nopGrant, String domain) {
        return new NopConnection<>(getProviderId(), nopGrant.getProviderUserId(),
                nopGrant.getUnionId(), nopGrant.getSecret(), nopGrant.getExpireTime(),
                getNopServiceProvider(), domain, getApiAdapter());
    }

    // region subclassing hooks
    protected String extractProviderUserId(NopGrant nopGrant) {
        return nopGrant.getProviderUserId();
    }
    // endregion

    // region internal helpers
    private NopServiceProvider<S> getNopServiceProvider() {
        return (NopServiceProvider<S>) getServiceProvider();
    }
    // endregion
}
