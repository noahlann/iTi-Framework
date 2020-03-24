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
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author NorthLan
 * @date 2020-03-24
 * @url https://noahlan.com
 */
@AllArgsConstructor
public class JdbcUsersConnectionRepository implements UsersConnectionRepository {
    private final JdbcTemplate jdbcTemplate;

    private final ConnectionFactoryLocator connectionFactoryLocator;

    private final TextEncryptor textEncryptor;

    private final String tablePrefix = "";

    @Override
    public List<String> findUserIdsWithConnection(Connection<?> connection) {
        ConnectionKey key = connection.getKey();
        List<String> localUserIds = jdbcTemplate.queryForList("select userId from " + tablePrefix + "UserConnection where providerId = ? and providerUserId = ?", String.class, key.getProviderId(), key.getProviderUserId());
//        if (localUserIds.size() == 0 && connectionSignUp != null) {
//            String newUserId = connectionSignUp.execute(connection);
//            if (newUserId != null)
//            {
//                createConnectionRepository(newUserId).addConnection(connection);
//                return Arrays.asList(newUserId);
//            }
//        }
        return localUserIds;
    }

    @Override
    public Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("providerId", providerId);
        parameters.addValue("providerUserIds", providerUserIds);
        final Set<String> localUserIds = new HashSet<String>();
        return new NamedParameterJdbcTemplate(jdbcTemplate).query("select userId from " + tablePrefix + "UserConnection where providerId = :providerId and providerUserId in (:providerUserIds)", parameters,
                new ResultSetExtractor<Set<String>>() {
                    public Set<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
                        while (rs.next()) {
                            localUserIds.add(rs.getString("userId"));
                        }
                        return localUserIds;
                    }
                });
    }

    @Override
    public ConnectionRepository createConnectionService(String userId) {
        return null;
    }
}
