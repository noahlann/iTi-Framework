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

package org.lan.iti.common.sequence.sequence.impl;

import org.lan.iti.common.core.util.Snowflake;
import org.lan.iti.common.sequence.exception.SequenceException;
import org.lan.iti.common.sequence.sequence.Sequence;

/**
 * 雪花算法生成器
 *
 * @author NorthLan
 * @date 2020-05-06
 * @url https://noahlan.com
 */
public class SnowflakeSequence implements Sequence {
    private Snowflake snowflake;

    public SnowflakeSequence(long workerId, long dataCenterId) {
        this.snowflake = new Snowflake(workerId, dataCenterId, true);
    }

    @Override
    public long next() throws SequenceException {
        try {
            return snowflake.nextId();
        } catch (IllegalStateException e) {
            throw new SequenceException(e.getMessage());
        }
    }

    @Override
    public String nextStr() throws SequenceException {
        return String.valueOf(next());
    }
}
