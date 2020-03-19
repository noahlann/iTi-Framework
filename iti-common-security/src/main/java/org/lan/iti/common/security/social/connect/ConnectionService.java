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

import org.lan.iti.common.security.social.connect.exception.NoSuchConnectionException;
import org.springframework.util.MultiValueMap;

import java.util.List;

/**
 * @author NorthLan
 * @date 2020-03-19
 * @url https://noahlan.com
 */
public interface ConnectionService {

    MultiValueMap<String, Connection<?>> findAllConnections();

    List<Connection<?>> findConnections(String providerId);

    <A> List<Connection<A>> findConnections(Class<A> apiType);

    MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUserIds);

    Connection<?> getConnection(ConnectionKey connectionKey) throws NoSuchConnectionException;

    <A> Connection<A> getConnection(Class<A> apiType, String providerUserId);

    <A> Connection<A> getPrimaryConnection(Class<A> apiType);

    <A> Connection<A> findPrimaryConnection(Class<A> apiType);

    void addConnection(Connection<?> connection);

    void updateConnection(Connection<?> connection);

    void removeConnections(String providerId);

    void removeConnection(ConnectionKey connectionKey);
}
