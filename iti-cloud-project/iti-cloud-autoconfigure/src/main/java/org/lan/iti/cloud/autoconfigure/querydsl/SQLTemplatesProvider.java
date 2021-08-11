/*
 *
 *  * Copyright (c) [2019-2021] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.cloud.autoconfigure.querydsl;

import com.querydsl.sql.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author NorthLan
 * @date 2021/8/9
 * @url https://blog.noahlan.com
 */
public class SQLTemplatesProvider {
    private final static Map<String, SQLTemplates> TEMPLATES = new HashMap<>();

    static {
        TEMPLATES.put("postgresql", PostgreSQLTemplates.builder().printSchema().build());
        TEMPLATES.put("mysql", MySQLTemplates.builder().printSchema().build());
        TEMPLATES.put("cubrid", CUBRIDTemplates.builder().printSchema().build());
        TEMPLATES.put("derby", DerbyTemplates.builder().printSchema().build());
        TEMPLATES.put("firebird", FirebirdTemplates.builder().printSchema().build());
        TEMPLATES.put("h2", H2Templates.builder().printSchema().build());
        TEMPLATES.put("hsqldb", HSQLDBTemplates.builder().printSchema().build());
        TEMPLATES.put("oracle", OracleTemplates.builder().printSchema().build());
        TEMPLATES.put("sqlite", SQLiteTemplates.builder().printSchema().build());
        TEMPLATES.put("sqlserver2005", SQLServer2005Templates.builder().printSchema().build());
        TEMPLATES.put("sqlserver2008", SQLServer2008Templates.builder().printSchema().build());
        TEMPLATES.put("sqlserver2012", SQLServer2012Templates.builder().printSchema().build());
    }

    public static SQLTemplates getSQLTemplates(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String productName = metaData.getDatabaseProductName();
            return TEMPLATES.getOrDefault(productName.toLowerCase(), SQLTemplates.DEFAULT);
        } catch (SQLException e) {
            return SQLTemplates.DEFAULT;
        }
    }
}
