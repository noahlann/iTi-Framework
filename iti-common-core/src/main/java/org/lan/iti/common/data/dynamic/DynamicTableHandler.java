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

package org.lan.iti.common.data.dynamic;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.parsers.ITableNameHandler;
import lombok.AllArgsConstructor;
import org.apache.ibatis.reflection.MetaObject;

/**
 * 动态表名配置
 * 优先使用前缀，若前缀为空，则使用后缀
 *
 * @author NorthLan
 * @date 2020-07-02
 * @url https://noahlan.com
 */
@AllArgsConstructor
public class DynamicTableHandler implements ITableNameHandler {
    private static final String PREFIX_TABLE_NAME = "delegate.boundSql.parameterObject.ew.tablePrefix";
    private static final String SUFFIX_TABLE_NAME = "delegate.boundSql.parameterObject.ew.tableSuffix";
    private static final String SCHEMA_SEPARATOR = "\\.";
    private final DynamicTableProperties.TableNameDict tableNameDict;

    @Override
    public String dynamicTableName(MetaObject metaObject, String sql, String tableName) {
        String tablePrefix = null;
        String tableSuffix = null;
        if (metaObject.hasGetter(PREFIX_TABLE_NAME)) {
            tablePrefix = toString(metaObject.getValue(PREFIX_TABLE_NAME));
        }
        if (metaObject.hasGetter(SUFFIX_TABLE_NAME)) {
            tableSuffix = toString(metaObject.getValue(SUFFIX_TABLE_NAME));
        }
        // 针对postgresql等数据库 提取出schema
        String schema = tableName.split(SCHEMA_SEPARATOR)[0];
        if (StrUtil.isBlank(schema)) {
            schema = StrUtil.EMPTY;
        } else {
            schema = schema + SCHEMA_SEPARATOR;
        }
        if (StrUtil.isNotBlank(tablePrefix)) {
            return schema + tablePrefix + "_" + tableNameDict.getName();
        } else if (StrUtil.isNotBlank(tableSuffix)) {
            return schema + tableNameDict.getName() + "_" + tableSuffix;
        }
        return null;
    }

    private String toString(Object object) {
        return null == object ? null : object.toString();
    }
}
