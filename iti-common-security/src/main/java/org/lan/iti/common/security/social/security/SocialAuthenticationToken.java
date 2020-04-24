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

package org.lan.iti.common.security.social.security;

import lombok.Getter;
import org.lan.iti.common.core.util.SystemClock;
import org.lan.iti.common.security.social.connect.Connection;
import org.lan.iti.common.security.social.connect.ConnectionData;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于社交身份验证的身份验证令牌，例如QQ、Wechat、Weibo或Gitee
 *
 * @author NorthLan
 * @date 2020-03-17
 * @url https://noahlan.com
 */
@Getter
public class SocialAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 5771733949969123629L;

    private final String providerId;

    private final Object principle;

    private final Connection<?> connection;

    private final Map<String, String> providerAccountData;

    public SocialAuthenticationToken(final Connection<?> connection, Map<String, String> providerAccountData) {
        super(null);
        Assert.notNull(connection, "connection must not be null");
        ConnectionData connectionData = connection.createData();
        Assert.notNull(connectionData.getProviderId(), "providerId must not be null");
        if (connectionData.getExpireTime() != null && connectionData.getExpireTime() < SystemClock.now()) {
            throw new IllegalArgumentException("连接已过期");
        }
        this.providerId = connectionData.getProviderId();
        this.connection = connection;
        this.principle = null; // 未授权情况下principal无效
        if (providerAccountData != null) {
            this.providerAccountData = Collections.unmodifiableMap(new HashMap<>(providerAccountData));
        } else {
            this.providerAccountData = Collections.emptyMap();
        }
        super.setAuthenticated(false);
    }

    public SocialAuthenticationToken(final Connection<?> connection, final Object details, final Map<String, String> providerAccountData, final Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        Assert.notNull(connection, "connection must not be null");
        this.connection = connection;
        ConnectionData connectionData = connection.createData();
        Assert.notNull(connectionData.getProviderId(), "providerId must not be null");
        this.providerId = connectionData.getProviderId();
        if (details == null) {
            throw new NullPointerException("details");
        }
        this.principle = details;
        if (providerAccountData != null) {
            this.providerAccountData = Collections.unmodifiableMap(new HashMap<>(providerAccountData));
        } else {
            this.providerAccountData = Collections.emptyMap();
        }
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principle;
    }

    @Override
    public void setAuthenticated(boolean authenticated) {
        if (!authenticated) {
            super.setAuthenticated(false);
        } else if (!super.isAuthenticated()) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
    }
}
