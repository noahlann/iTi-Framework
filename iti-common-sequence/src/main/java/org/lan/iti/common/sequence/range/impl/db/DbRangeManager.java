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

package org.lan.iti.common.sequence.range.impl.db;

import cn.hutool.core.util.StrUtil;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.lan.iti.common.core.util.Formatter;
import org.lan.iti.common.core.util.SystemClock;
import org.lan.iti.common.sequence.exception.SequenceException;
import org.lan.iti.common.sequence.range.Range;
import org.lan.iti.common.sequence.range.RangeManager;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.Timestamp;

/**
 * 基于DB的区间管理器
 *
 * @author NorthLan
 * @date 2020-05-06
 * @url https://noahlan.com
 */
public class DbRangeManager implements RangeManager {
    private final static String TABLE_NAME_PLACEHOLDER = "#tableName";

    // SQL
    private static final long DELTA = 100000000L;
    private final static String ALL_FIELDS = "name, value, create_time, update_time";
    private final static String SQL_INSERT_RANGE = "INSERT INTO " + TABLE_NAME_PLACEHOLDER + "(" + ALL_FIELDS + ") VALUES (?,?,?,?)";
    private final static String SQL_UPDATE_RANGE = "UPDATE " + TABLE_NAME_PLACEHOLDER + " SET value=?, update_time=? WHERE name=? AND value=?";
    private final static String SQL_SELECT_RANGE = "SELECT value FROM " + TABLE_NAME_PLACEHOLDER + " WHERE name=?";
    /**
     * 区间步长
     */
    @Setter
    @Accessors(chain = true)
    private int step = 1000;

    /**
     * 区间起始位置，真实从stepStart+1开始
     */
    @Setter
    @Accessors(chain = true)
    private long stepStart = 0;

    /**
     * 获取区间失败重试次数
     */
    @Setter
    @Accessors(chain = true)
    private int retryTimes = 1;

    /**
     * 表名
     */
    @Setter
    @Accessors(chain = true)
    private String tableName = "iti_sequence";

    /**
     * 模式名
     */
    @Setter
    @Accessors(chain = true)
    private String schema = "public";

    /**
     * 真实表名称
     */
    private String realTableName;
    private final JdbcTemplate jdbcTemplate;

    public DbRangeManager(DataSource dataSource) {
        Assert.notNull(dataSource, "DataSource required");
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Range nextRange(String name) throws SequenceException {
        if (StrUtil.isBlank(name)) {
            throw new SequenceException("[DbRangeManager] name is empty.");
        }
        Long oldValue;
        long newValue;
        for (int i = 0; i < retryTimes; ++i) {
            oldValue = selectRange(name);
            if (null == oldValue) {
                // 区间不存在,重新执行
                continue;
            }
            newValue = oldValue + step;
            if (updateRange(name, newValue, oldValue)) {
                return new Range(oldValue + 1, newValue);
            }
        }
        throw new SequenceException(Formatter.format("Retried too many times, retryTimes = {}", retryTimes));
    }

    @Override
    public void init() {
        checkParam();
    }

    // region sql

    /**
     * 插入数据区间
     *
     * @param name 区间名称
     */
    private boolean insertRange(String name) {
        try {
            int affectRows = jdbcTemplate.update(SQL_INSERT_RANGE.replace(TABLE_NAME_PLACEHOLDER, getRealTableName()),
                    name, stepStart, new Timestamp(SystemClock.now()), new Timestamp(SystemClock.now()));
            return affectRows > 0;
        } catch (DataAccessException e) {
            throw new SequenceException(e);
        }
    }

    /**
     * 更新区间，乐观策略
     *
     * @param name 区间名称
     */
    private boolean updateRange(String name, Long newValue, Long oldValue) {
        try {
            int affectRows = jdbcTemplate.update(SQL_UPDATE_RANGE.replace(TABLE_NAME_PLACEHOLDER, getRealTableName()),
                    newValue, new Timestamp(SystemClock.now()), name, oldValue);
            return affectRows > 0;
        } catch (DataAccessException e) {
            throw new SequenceException(e);
        }
    }

    /**
     * 查询区间，如果区间不存在，会新增一个区间，并返回null，由上层重新执行
     *
     * @return 区间值
     */
    private Long selectRange(String name) {
        Long oldValue;
        try {
            oldValue = jdbcTemplate.query(
                    SQL_SELECT_RANGE.replace(TABLE_NAME_PLACEHOLDER, getRealTableName()),
                    new String[]{name},
                    rs -> {
                        if (rs.next()) {
                            return rs.getLong("value");
                        } else {
                            return null;
                        }
                    });
            if (oldValue == null) {
                // 需要初始化
                insertRange(name);
                return null;
            }
            if (oldValue < 0) {
                throw new SequenceException(
                        Formatter.format("Sequence value cannot be less than zero, value = {}, please check table sequence {}",
                                oldValue, getRealTableName()));
            }
            if (oldValue > Long.MAX_VALUE - DELTA) {
                throw new SequenceException(
                        Formatter.format("Sequence value overflow, value = {}, please check table sequence {}",
                                oldValue, getRealTableName()));
            }
            return oldValue;
        } catch (DataAccessException e) {
            throw new SequenceException(e);
        }
    }
    // endregion

    private String getRealTableName() {
        if (StrUtil.isBlank(realTableName)) {
            realTableName = schema + "." + tableName;
        }
        return realTableName;
    }

    private void checkParam() {
        if (step <= 0) {
            throw new SequenceException("[DbRangeManager] step must greater than 0.");
        }
        if (stepStart < 0) {
            throw new SequenceException("[DbRangeManager] stepStart < 0.");
        }
        if (retryTimes <= 0) {
            throw new SequenceException("[DbRangeManager] retryTimes must greater than 0.");
        }
    }
}
