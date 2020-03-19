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

import lombok.Setter;
import org.lan.iti.common.security.social.connect.*;
import org.lan.iti.common.security.social.connect.exception.NoSuchConnectionException;

import java.util.*;

/**
 * @author NorthLan
 * @date 2020-03-19
 * @url https://noahlan.com
 */
public class InMemoryUsersConnectionServiceImpl implements UsersConnectionService {
    private ConnectionFactoryLocator connectionFactoryLocator;
    private Map<String, ConnectionService> connectionServices;

    @Setter
    private ConnectionSignUp connectionSignUp;

    public InMemoryUsersConnectionServiceImpl(ConnectionFactoryLocator connectionFactoryLocator) {
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.connectionServices = new HashMap<>();
    }

    @Override
    public List<String> findUserIdsWithConnection(Connection<?> connection) {
        List<String> localUserIds = new ArrayList<>();
        Set<Map.Entry<String, ConnectionService>> connectionServiceEntries = connectionServices.entrySet();
        for (Map.Entry<String, ConnectionService> entry : connectionServiceEntries) {
            try {
                entry.getValue().getConnection(connection.getKey());
                localUserIds.add(entry.getKey());
            } catch (NoSuchConnectionException e) {
                // just catch it.
            }
        }
        if (localUserIds.size() == 0 && connectionSignUp != null) {
            // 注册
            String newUserId = connectionSignUp.execute(connection);
            if (newUserId != null) {
                createConnectionService(newUserId).addConnection(connection);
                return Collections.singletonList(newUserId);
            }
        }
        return localUserIds;
    }

    @Override
    public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
        List<String> localUserIds = new ArrayList<>();
        Set<Map.Entry<String, ConnectionService>> connectionServiceEntries = connectionServices.entrySet();
        for (Map.Entry<String, ConnectionService> entry : connectionServiceEntries) {
            String localUserId = entry.getKey();
            List<Connection<?>> providerConnections = entry.getValue().findConnections(providerId);
            for (Connection<?> connection : providerConnections) {
                if (providerUserIds.contains(connection.getKey().getProviderUserId())) {
                    localUserIds.add(localUserId);
                }
            }
        }
        return new HashSet<>(localUserIds);
    }

    @Override
    public ConnectionService createConnectionService(String userId) {
        if (!connectionServices.containsKey(userId)) {
            connectionServices.put(userId, new InMemoryConnectionServiceImpl(connectionFactoryLocator));
        }
        return connectionServices.get(userId);
    }
}
