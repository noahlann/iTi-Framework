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

import lombok.Getter;
import lombok.Setter;
import org.lan.iti.common.security.social.ServiceProvider;
import org.lan.iti.common.security.social.connect.ApiAdapter;
import org.lan.iti.common.security.social.connect.Connection;
import org.lan.iti.common.security.social.connect.ConnectionData;
import org.lan.iti.common.security.social.connect.ConnectionFactory;
import org.lan.iti.common.security.social.oauth2.AccessGrant;
import org.lan.iti.common.security.social.oauth2.OAuth2ServiceProvider;

import java.util.UUID;

/**
 * 基于 oAuth 2.0 协议的 连接工厂
 *
 * @author NorthLan
 * @date 2020-03-30
 * @url https://noahlan.com
 */
public class OAuth2ConnectionFactory<S> extends ConnectionFactory<S> {

    @Getter
    @Setter
    private String scope = null;

    public OAuth2ConnectionFactory(String providerId, ServiceProvider<S> serviceProvider, ApiAdapter<S> apiAdapter) {
        super(providerId, serviceProvider, apiAdapter);
    }

    @Override
    public Connection<S> createConnection(ConnectionData data) {
        return new OAuth2Connection<>(data, getOAuth2ServiceProvider(), getApiAdapter());
    }

    public Connection<S> createConnection(AccessGrant accessGrant, String domain) {
        return new OAuth2Connection<>(getProviderId(), extractProviderUserId(accessGrant),
                accessGrant.getAccessToken(), accessGrant.getRefreshToken(), accessGrant.getExpireTime(), null,
                getOAuth2ServiceProvider(), domain, getApiAdapter());
    }

    public String generateState() {
        return UUID.randomUUID().toString();
    }

    public boolean supportsStateParameter() {
        return true;
    }

    // region subclassing hooks
    protected String extractProviderUserId(AccessGrant accessGrant) {
        return null;
    }
    // endregion

    // region internal helpers
    private OAuth2ServiceProvider<S> getOAuth2ServiceProvider() {
        return (OAuth2ServiceProvider<S>) getServiceProvider();
    }
    // endregion
}
