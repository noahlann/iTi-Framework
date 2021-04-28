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

package org.lan.iti.cloud.sequence.sequence.impl;

import org.lan.iti.cloud.sequence.exception.SequenceException;
import org.lan.iti.cloud.sequence.sequence.Sequence;
import org.lan.iti.common.core.util.idgen.DefaultIdGenerator;
import org.lan.iti.common.core.util.idgen.IGenerator;
import org.lan.iti.common.core.util.idgen.exception.IdGeneratorException;
import org.lan.iti.common.core.util.idgen.snowflake.SnowflakeOptions;

/**
 * 雪花算法生成器
 *
 * @author NorthLan
 * @date 2020-05-06
 * @url https://noahlan.com
 */
public class SnowflakeSequence implements Sequence {
    private final IGenerator snowflake;

    public SnowflakeSequence(long baseTime, long workerId, boolean useSystemClock) {
        this.snowflake = new DefaultIdGenerator(new SnowflakeOptions()
                .setBaseTime(baseTime)
                .setWorkerId(workerId)
                .setUseSystemClock(useSystemClock));
    }

    @Override
    public long next() throws SequenceException {
        try {
            return snowflake.nextLong();
        } catch (IdGeneratorException e) {
            throw new SequenceException(e.getMessage());
        }
    }

    @Override
    public String nextStr() throws SequenceException {
        return String.valueOf(next());
    }

    @Override
    public String nextStr(String format) throws SequenceException {
        return String.format(format, next());
    }
}
