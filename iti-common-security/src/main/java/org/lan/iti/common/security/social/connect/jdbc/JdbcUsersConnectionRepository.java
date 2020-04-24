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

import cn.hutool.core.util.StrUtil;
import lombok.Setter;
import org.lan.iti.common.security.social.connect.*;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 多用户连接仓库
 *
 * @author NorthLan
 * @date 2020-03-24
 * @url https://noahlan.com
 */
@SuppressWarnings("ConstantConditions")
public class JdbcUsersConnectionRepository extends JdbcDaoSupport implements UsersConnectionRepository {
    public static final String DEFAULT_CONNECTION_SCHEMA = "public";
    public static final String DEFAULT_CONNECTION_TABLE_NAME = "user_connection";

    private final ConnectionFactoryLocator connectionFactoryLocator;
    private final TextEncryptor textEncryptor;

    @Setter
    private String schema;

    @Setter
    private String tableName;

    @Setter
    private ConnectionSignUp connectionSignUp;

    public JdbcUsersConnectionRepository(DataSource dataSource,
                                         ConnectionFactoryLocator connectionFactoryLocator,
                                         TextEncryptor textEncryptor) {
        setDataSource(dataSource);
        this.connectionFactoryLocator = connectionFactoryLocator;
        this.textEncryptor = textEncryptor;
        // default values
        this.schema = DEFAULT_CONNECTION_SCHEMA;
        this.tableName = DEFAULT_CONNECTION_TABLE_NAME;
    }

    @Override
    public List<String> findUserIdsWithConnection(Connection<?> connection) {
        ConnectionKey key = connection.getKey();
        List<String> localUserIds = getJdbcTemplate().queryForList("select user_id from " + getTableName() + " where provider_id = ? and provider_user_id = ?",
                String.class, key.getProviderId(), key.getProviderUserId());
//        if (localUserIds.size() == 0 && connectionSignUp != null) {
//            String newUserId = connectionSignUp.execute(connection);
//            if (newUserId != null) {
//                createConnectionRepository(newUserId).addConnection(connection);
//                return Collections.singletonList(newUserId);
//            }
//        }
        return localUserIds;
    }

    @Override
    public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("providerId", providerId);
        parameters.addValue("providerUserIds", providerUserIds);
        final Set<String> localUserIds = new HashSet<>();
        return new NamedParameterJdbcTemplate(getJdbcTemplate())
                .query("select user_id from " + getTableName() + " where provider_id = :providerId and provider_user_id in (:providerUserIds)"
                        , parameters,
                        rs -> {
                            while (rs.next()) {
                                localUserIds.add(rs.getString("user_id"));
                            }
                            return localUserIds;
                        });
    }

    @Override
    public ConnectionRepository createConnectionRepository(ConnectionUserKey userKey) {
        if (userKey == null) {
            throw new IllegalArgumentException("userKey cannot be null");
        }
        if (StrUtil.isBlank(userKey.getUserId())) {
            throw new IllegalArgumentException("userId cannot be null or blank");
        }
        if (StrUtil.isBlank(userKey.getDomain())) {
            throw new IllegalArgumentException("domain cannot be null or blank");
        }
        return new JdbcConnectionRepository(userKey, getJdbcTemplate(), connectionFactoryLocator, textEncryptor, schema, tableName);
    }

    // region internal helpers
    private String getTableName() {
        return schema + tableName;
    }
    // endregion
}
