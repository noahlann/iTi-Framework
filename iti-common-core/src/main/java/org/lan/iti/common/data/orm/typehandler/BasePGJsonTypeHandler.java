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

import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 扩展类型处理器 抽象类
 * <pre>
 *     快速处理 postgres 的 json / jsonb 等类型
 * </pre>
 *
 * @author NorthLan
 * @date 2020-05-28
 * @url https://noahlan.com
 */
public abstract class BasePGJsonTypeHandler<T> extends BaseTypeHandler<T> {
    protected static final String JSON = "json";
    protected static final String JSONB = "jsonb";

    protected abstract String getType();

    protected abstract String convertValue(T parameter);

    protected abstract T getValue(PGobject pGobject);

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        PGobject pGobject = new PGobject();
        pGobject.setType(getType());
        pGobject.setValue(convertValue(parameter));
        ps.setObject(i, pGobject);
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        PGobject pGobject = rs.getObject(columnName, PGobject.class);
        if (pGobject == null) {
            return null;
        }
        if (StrUtil.equalsIgnoreCase(pGobject.getType(), getType())) {
            return getValue(pGobject);
        }
        return (T) rs.getObject(columnName);
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        PGobject pGobject = rs.getObject(columnIndex, PGobject.class);
        if (pGobject == null) {
            return null;
        }
        if (StrUtil.equalsIgnoreCase(pGobject.getType(), getType())) {
            return getValue(pGobject);
        }
        return (T) rs.getObject(columnIndex);
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        PGobject pGobject = cs.getObject(columnIndex, PGobject.class);
        if (pGobject == null) {
            return null;
        }
        if (StrUtil.equalsIgnoreCase(pGobject.getType(), getType())) {
            return getValue(pGobject);
        }
        return (T) cs.getObject(columnIndex);
    }
}
