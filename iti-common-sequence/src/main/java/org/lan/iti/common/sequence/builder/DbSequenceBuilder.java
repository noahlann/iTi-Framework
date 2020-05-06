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

package org.lan.iti.common.sequence.builder;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.sequence.properties.SequenceDbProperties;
import org.lan.iti.common.sequence.range.Name;
import org.lan.iti.common.sequence.sequence.Sequence;
import org.lan.iti.common.sequence.sequence.impl.DefaultRangeSequence;

import javax.sql.DataSource;

/**
 * 数据库发号器
 * 基于Db取步长
 *
 * @author NorthLan
 * @date 2020-05-06
 * @url https://noahlan.com
 */
@Data
@Accessors(chain = true)
@Slf4j
public class DbSequenceBuilder implements SequenceBuilder {

    /**
     * 数据库数据源[必选]
     */
    private DataSource dataSource;

    /**
     * 业务名称[必选]
     */
    private Name name;

    private SequenceDbProperties properties;

    public static DbSequenceBuilder create() {
        return new DbSequenceBuilder();
    }

    @Override
    public Sequence build() {
        DefaultRangeSequence sequence = new DefaultRangeSequence();
        sequence.setName(this.name);
        log.warn("功能未完成,请勿使用");
        return sequence;
    }

    public DbSequenceBuilder properties(SequenceDbProperties properties) {
        this.properties = properties;
        return this;
    }
}
