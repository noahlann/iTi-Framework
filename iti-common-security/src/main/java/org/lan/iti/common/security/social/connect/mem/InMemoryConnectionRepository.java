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

package org.lan.iti.common.security.social.connect.mem;

import org.lan.iti.common.security.social.connect.Connection;
import org.lan.iti.common.security.social.connect.ConnectionFactoryLocator;
import org.lan.iti.common.security.social.connect.ConnectionKey;
import org.lan.iti.common.security.social.connect.ConnectionRepository;
import org.lan.iti.common.security.social.connect.exception.DuplicateConnectionException;
import org.lan.iti.common.security.social.connect.exception.NoSuchConnectionException;
import org.lan.iti.common.security.social.connect.exception.NotConnectedException;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 内存 Connection 实现
 *
 * @author NorthLan
 * @date 2020-03-19
 * @url https://noahlan.com
 */
public class InMemoryConnectionRepository implements ConnectionRepository {
    // <providerId, Connection<provider API>>
    private MultiValueMap<String, Connection<?>> connections;
    private ConnectionFactoryLocator connectionFactoryLocator;

    public InMemoryConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.connections = new LinkedMultiValueMap<>();
    }

    @Override
    public MultiValueMap<String, Connection<?>> findAllConnections() {
        return connections;
    }

    @Override
    public List<Connection<?>> findConnections(String providerId) {
        return connections.getOrDefault(providerId, Collections.emptyList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A> List<Connection<A>> findConnections(Class<A> apiType) {
        List<?> providerConnections = findConnections(getProviderId(apiType));
        return (List<Connection<A>>) providerConnections;
    }

    @Override
    public MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUserIds) {
        Assert.notEmpty(providerUserIds, "providerUserIds must not be null.");
        MultiValueMap<String, Connection<?>> connectionsToUsers = new LinkedMultiValueMap<>(providerUserIds.size());
        for (Map.Entry<String, List<String>> entry : providerUserIds.entrySet()) {
            String providerId = entry.getKey();
            List<String> userIds = entry.getValue();
            if (connections.containsKey(providerId)) {
                List<Connection<?>> providerConnections = connections.get(providerId);
                for (Connection<?> connection : providerConnections) {
                    if (userIds.contains(connection.getKey().getProviderUserId())) {
                        connectionsToUsers.add(providerId, connection);
                    }
                }
            }
        }
        return connectionsToUsers;
    }

    @Override
    public Connection<?> getConnection(ConnectionKey connectionKey) {
        if (connections.containsKey(connectionKey.getProviderId())) {
            List<Connection<?>> providerConnections = connections.get(connectionKey.getProviderId());
            for (Connection<?> connection : providerConnections) {
                if (connection.getKey().equals(connectionKey)) {
                    return connection;
                }
            }
        }
        throw new NoSuchConnectionException(connectionKey);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A> Connection<A> getConnection(Class<A> apiType, String providerUserId) {
        return (Connection<A>) getConnection(new ConnectionKey(getProviderId(apiType), providerUserId));
    }

    @Override
    @NonNull
    public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
        Connection<A> primaryConnection = findPrimaryConnection(apiType);
        if (primaryConnection == null) {
            throw new NotConnectedException(getProviderId(apiType));
        }
        return primaryConnection;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
        String providerId = getProviderId(apiType);
        if (connections.containsKey(providerId)) {
            return (Connection<A>) connections.get(providerId).get(0);
        }
        return null;
    }

    @Override
    public void addConnection(Connection<?> connection) {
        ConnectionKey connectionKey = connection.getKey();
        try {
            getConnection(connectionKey);
            throw new DuplicateConnectionException(connectionKey);
        } catch (NoSuchConnectionException e) {
            connections.add(connection.createData().getProviderId(), connection);
        }
    }

    @Override
    public void updateConnection(Connection<?> connection) {
        connections.add(connection.createData().getProviderId(), connection);
    }

    @Override
    public void removeConnections(String providerId) {
        connections.remove(providerId);
    }

    @Override
    public void removeConnection(ConnectionKey connectionKey) {
        String providerId = connectionKey.getProviderId();
        if (connections.containsKey(providerId)) {
            List<Connection<?>> providerConnections = connections.get(providerId);
            providerConnections.removeIf(connection -> connection.getKey().equals(connectionKey));
        }
    }

    private <A> String getProviderId(Class<A> apiType) {
        return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId();
    }
}
