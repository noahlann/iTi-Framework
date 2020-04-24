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

package org.lan.iti.common.security.properties;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * 客户端信息
 *
 * @author NorthLan
 * @date 2020-04-13
 * @url https://noahlan.com
 */
@Data
public class OAuth2ClientDetailsProperties {

    // region default
    private static final String CLIENT_FIELDS_FOR_UPDATE = "resource_ids, scope, " +
            "authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, " +
            "refresh_token_validity, additional_information, autoapprove";

    private static final String CLIENT_FIELDS = "client_id, CONCAT('{noop}',client_secret) as client_secret, " + CLIENT_FIELDS_FOR_UPDATE;

    private static final String DEFAULT_SCHEMA = "";
    private static final String DEFAULT_TABLE_PREFIX = "";
    private static final String DEFAULT_TABLE_NAME = "oauth_client_details";
    // endregion

    /**
     * 模式名
     */
    private String schema = DEFAULT_SCHEMA;

    /**
     * 表前缀
     */
    private String tablePrefix = DEFAULT_TABLE_PREFIX;

    /**
     * 表名
     */
    private String tableName = DEFAULT_TABLE_NAME;

    // region SQL

    /**
     * JdbcClientDetailsService 语句
     */
    public String getDeleteClientDetailsSql() {
        return "delete from " + propTable() + " where client_id = ?";
    }

    private String getBaseFindStatement() {
        return "select " + CLIENT_FIELDS + " from " + propTable();
    }

    public String getFindClientDetailsSql() {
        return getBaseFindStatement() + " order by client_id";
    }

    public String getUpdateClientDetailsSql() {
        return "update " + propTable() + " set " + CLIENT_FIELDS_FOR_UPDATE.replaceAll(", ", "=?, ") + "=? where client_id = ?";
    }

    public String getUpdateClientSecretSql() {
        return "update " + propTable() + " set client_secret = ? where client_id = ?";
    }

    public String getInsertClientDetailsSql() {
        return "insert into " + propTable() + " (" + CLIENT_FIELDS + ") values (?,?,?,?,?,?,?,?,?,?,?)";
    }

    public String getSelectClientDetailsSql() {
        return getBaseFindStatement() + " where client_id = ?";
    }
    // endregion

    private String propTable() {
        StringBuilder sb = new StringBuilder();
        if (StrUtil.isNotBlank(schema)) {
            sb.append(schema);
            sb.append(".");
        }
        if (StrUtil.isNotBlank(tablePrefix)) {
            sb.append(tablePrefix);
        }
        if (StrUtil.isNotBlank(tableName)) {
            sb.append(tableName);
        } else {
            sb.append(DEFAULT_TABLE_NAME);
        }
        return sb.toString();
    }
}
