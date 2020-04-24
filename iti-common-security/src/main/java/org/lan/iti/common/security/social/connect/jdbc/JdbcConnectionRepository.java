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

package org.lan.iti.common.security.social.connect.jdbc;

import lombok.AllArgsConstructor;
import org.lan.iti.common.security.social.connect.*;
import org.lan.iti.common.security.social.connect.exception.DuplicateConnectionException;
import org.lan.iti.common.security.social.connect.exception.NoSuchConnectionException;
import org.lan.iti.common.security.social.connect.exception.NotConnectedException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 用户-连接 仓库
 * <p>
 * 一个用户一个连接
 * </p>
 *
 * @author NorthLan
 * @date 2020-03-24
 * @url https://noahlan.com
 */
@SuppressWarnings({"unchecked", "SqlDialectInspection"})
@AllArgsConstructor
public class JdbcConnectionRepository implements ConnectionRepository {
    private final ConnectionUserKey userKey;

    private final JdbcTemplate jdbcTemplate;
    private final ConnectionFactoryLocator connectionFactoryLocator;
    private final TextEncryptor textEncryptor;
    private final String tableName;
    private final String schema;

    @Override
    public MultiValueMap<String, Connection<?>> findAllConnections() {
        List<Connection<?>> resultList = jdbcTemplate.query(selectFromUserConnection() + " WHERE user_id = ? AND `domain` = ? ",
                connectionMapper, userKey.getUserId(), userKey.getDomain());
        MultiValueMap<String, Connection<?>> connections = new LinkedMultiValueMap<>();
        Set<String> registeredProviderIds = connectionFactoryLocator.registeredProviderIds();
        for (String registeredProviderId : registeredProviderIds) {
            connections.put(registeredProviderId, Collections.emptyList());
        }
        for (Connection<?> connection : resultList) {
            String providerId = connection.getKey().getProviderId();
            if (!connections.containsKey(providerId)) {
                connections.put(providerId, new LinkedList<>());
            }
            if (connections.get(providerId).size() == 0) {
                connections.put(providerId, new LinkedList<>());
            }
            connections.add(providerId, connection);
        }
        return connections;
    }

    @Override
    public List<Connection<?>> findConnections(String providerId) {
        return jdbcTemplate.query(selectFromUserConnection() + " where user_id = ? and `domain` = ? and provider_id = ?",
                connectionMapper, userKey.getUserId(), userKey.getDomain(), providerId);
    }

    @Override
    public <A> List<Connection<A>> findConnections(Class<A> apiType) {
        List<?> connections = findConnections(getProviderId(apiType));
        return (List<Connection<A>>) connections;
    }

    @Override
    public MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUserIds) {
        if (providerUserIds == null || providerUserIds.isEmpty()) {
            throw new IllegalArgumentException("Unable to execute find: no providerUsers provided");
        }
        StringBuilder providerUsersCriteriaSql = new StringBuilder();
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("userId", userKey.getUserId());
        for (Iterator<Map.Entry<String, List<String>>> it = providerUserIds.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, List<String>> entry = it.next();
            String providerId = entry.getKey();
            providerUsersCriteriaSql.append("provider_id = :providerId_").append(providerId).append(" and provider_user_id in (:providerUserIds_").append(providerId).append(")");
            parameters.addValue("providerId_" + providerId, providerId);
            parameters.addValue("providerUserIds_" + providerId, entry.getValue());
            if (it.hasNext()) {
                providerUsersCriteriaSql.append(" or ");
            }
        }
        List<Connection<?>> resultList = new NamedParameterJdbcTemplate(jdbcTemplate).query(
                selectFromUserConnection() + " where user_id = :userId and " + providerUsersCriteriaSql + " order by provider_id",
                parameters, connectionMapper);
        MultiValueMap<String, Connection<?>> connectionsForUsers = new LinkedMultiValueMap<String, Connection<?>>();
        for (Connection<?> connection : resultList) {
            String providerId = connection.getKey().getProviderId();
            List<String> userIds = providerUserIds.get(providerId);
            List<Connection<?>> connections = connectionsForUsers.get(providerId);
            if (connections == null) {
                connections = new ArrayList<>(userIds.size());
                for (int i = 0; i < userIds.size(); i++) {
                    connections.add(null);
                }
                connectionsForUsers.put(providerId, connections);
            }
            String providerUserId = connection.getKey().getProviderUserId();
            int connectionIndex = userIds.indexOf(providerUserId);
            connections.set(connectionIndex, connection);
        }
        return connectionsForUsers;
    }

    @Override
    public Connection<?> getConnection(ConnectionKey connectionKey) throws NoSuchConnectionException {
        try {
            return jdbcTemplate.queryForObject(selectFromUserConnection() + " where user_id = ? and `domain` = ? and provider_id = ? and provider_user_id = ?",
                    connectionMapper,
                    userKey.getUserId(), userKey.getDomain(),
                    connectionKey.getProviderId(), connectionKey.getProviderUserId());
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchConnectionException(connectionKey);
        }
    }

    @Override
    public <A> Connection<A> getConnection(Class<A> apiType, String providerUserId) {
        String providerId = getProviderId(apiType);
        return (Connection<A>) getConnection(new ConnectionKey(providerId, providerUserId));
    }

    @Override
    public <A> Connection<A> getPrimaryConnection(Class<A> apiType) {
        String providerId = getProviderId(apiType);
        Connection<A> connection = (Connection<A>) findPrimaryConnection(providerId);
        if (connection == null) {
            throw new NotConnectedException(providerId);
        }
        return connection;
    }

    @Override
    public <A> Connection<A> findPrimaryConnection(Class<A> apiType) {
        String providerId = getProviderId(apiType);
        return (Connection<A>) findPrimaryConnection(providerId);
    }

    @Override
    @Transactional
    public void addConnection(Connection<?> connection) {
        try {
            ConnectionData data = connection.createData();
            jdbcTemplate.update("insert into " + getTableName() + " (user_id, provider_id, provider_user_id, display_name, profileUrl, imageUrl, accessToken, secret, refreshToken, expireTime) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    userKey.getUserId(), data.getProviderId(), data.getProviderUserId(), data.getDisplayName(), data.getProfileUrl(), data.getImageUrl(), encrypt(data.getAccessToken()), encrypt(data.getSecret()), encrypt(data.getRefreshToken()), data.getExpireTime());
        } catch (DuplicateKeyException e) {
            throw new DuplicateConnectionException(connection.getKey());
        }
    }

    @Override
    @Transactional
    public void updateConnection(Connection<?> connection) {
        ConnectionData data = connection.createData();
        jdbcTemplate.update("update " + getTableName() + " set display_name = ?, profile_url = ?, image_url = ?, access_token = ?, secret = ?, refresh_token = ?, expire_time = ? where user_id = ? and `domain` = ? and provider_id = ? and provider_user_id = ?",
                data.getDisplayName(),
                data.getProfileUrl(),
                data.getImageUrl(),
                encrypt(data.getAccessToken()),
                encrypt(data.getSecret()),
                encrypt(data.getRefreshToken()),
                data.getExpireTime(),
                userKey.getUserId(),
                userKey.getDomain(),
                data.getProviderId(),
                data.getProviderUserId());
    }

    @Override
    @Transactional
    public void removeConnections(String providerId) {
        jdbcTemplate.update("delete from " + getTableName() + " where user_id = ? and `domain` = ? and provider_id = ?",
                userKey.getUserId(), userKey.getDomain(), providerId);
    }

    @Override
    @Transactional
    public void removeConnection(ConnectionKey connectionKey) {
        jdbcTemplate.update("delete from ? where user_id = ? and `domain` = ? and provider_id = ? and provider_user_id = ?",
                getTableName(), userKey.getUserId(), userKey.getDomain(), connectionKey.getProviderId(), connectionKey.getProviderUserId());
    }

    // region internal helpers
    private String getAllColumnStr() {
        return "user_id, provider_id, provider_user_id, domain, display_name, profile_url, image_url, access_token, secret, refresh_token, expire_time, union_id";
    }

    private String selectFromUserConnection() {
        return "select " + getAllColumnStr() + " from " + getTableName();
    }

    private String getTableName() {
        return schema + tableName;
    }

    private Connection<?> findPrimaryConnection(String providerId) {
        List<Connection<?>> connections = jdbcTemplate.query(selectFromUserConnection() + " where user_id = ? and `domain` = ? and provider_id = ?",
                connectionMapper, userKey.getUserId(), userKey.getDomain(), providerId);
        if (connections.size() > 0) {
            return connections.get(0);
        } else {
            return null;
        }
    }

    private <A> String getProviderId(Class<A> apiType) {
        return connectionFactoryLocator.getConnectionFactory(apiType).getProviderId();
    }

    private String encrypt(String text) {
        return text != null ? textEncryptor.encrypt(text) : null;
    }


    private final ServiceProviderConnectionMapper connectionMapper = new ServiceProviderConnectionMapper();

    private final class ServiceProviderConnectionMapper implements RowMapper<Connection<?>> {

        @Override
        public Connection<?> mapRow(@NonNull ResultSet resultSet, int i) throws SQLException {
            ConnectionData connectionData = mapConnectionData(resultSet);
            ConnectionFactory<?> connectionFactory = connectionFactoryLocator.getConnectionFactory(connectionData.getProviderId());
            return connectionFactory.createConnection(connectionData);
        }

        private ConnectionData mapConnectionData(ResultSet rs) throws SQLException {
            return new ConnectionData(
                    rs.getString("user_id"),
                    rs.getString("domain"),
                    rs.getString("provider_id"),
                    rs.getString("provider_user_id"),
                    rs.getString("display_name"),
                    rs.getString("profile_url"),
                    rs.getString("image_url"),
                    decrypt(rs.getString("access_token")),
                    decrypt(rs.getString("secret")),
                    decrypt(rs.getString("refresh_token")),
                    expireTime(rs.getLong("expire_time")),
                    rs.getString("union_id"));
        }

        private String decrypt(String encryptedText) {
            return encryptedText != null ? textEncryptor.decrypt(encryptedText) : null;
        }

        private Long expireTime(long expireTime) {
            return expireTime == 0 ? null : expireTime;
        }
    }
    // endregion
}
