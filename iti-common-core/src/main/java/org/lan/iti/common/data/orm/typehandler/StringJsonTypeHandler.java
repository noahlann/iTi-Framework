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

package org.lan.iti.common.data.orm.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Json 类型转换器
 * <p>
 * 插入/更新时封装为PGobject对象
 * 查询时封装为PGobject对象后提取value字段
 * </p>
 *
 * @author NorthLan
 * @date 2020-04-09
 * @url https://noahlan.com
 */
public class StringJsonTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, String s, JdbcType jdbcType) throws SQLException {
        PGobject pGobject = new PGobject();
        pGobject.setType("json");
        pGobject.setValue(s);
        preparedStatement.setObject(i, pGobject);
    }

    @Override
    public String getNullableResult(ResultSet resultSet, String s) throws SQLException {
        PGobject pGobject = resultSet.getObject(s, PGobject.class);
        if (pGobject != null) {
            return pGobject.getValue();
        }
        return resultSet.getString(s);
    }

    @Override
    public String getNullableResult(ResultSet resultSet, int i) throws SQLException {
        PGobject pGobject = resultSet.getObject(i, PGobject.class);
        if (pGobject != null) {
            return pGobject.getValue();
        }
        return resultSet.getString(i);
    }

    @Override
    public String getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        PGobject pGobject = callableStatement.getObject(i, PGobject.class);
        if (pGobject != null) {
            return pGobject.getValue();
        }
        return callableStatement.getString(i);
    }
}
