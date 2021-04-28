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

package org.lan.iti.common.core.util.idgen.snowflake;

import org.lan.iti.common.core.util.idgen.exception.IdGeneratorException;

/**
 * 雪花漂移算法
 *
 * @author NorthLan
 * @date 2021-04-27
 * @url https://noahlan.com
 */
public class ShiftedSnowflake extends Snowflake {

    public ShiftedSnowflake() {
        super();
    }

    public ShiftedSnowflake(SnowflakeOptions options) {
        super(options);
    }

    @Override
    public long nextLong() {
        synchronized (syncLock) {
            long currentTimeTick = getCurrentTimeTick();

            if (lastTimeTick == currentTimeTick) {
                if (currentSeqNumber++ > maxSeqNumber) {
                    this.currentSeqNumber = minSeqNumber;
                    currentTimeTick = getNextTimeTick();
                }
            } else {
                this.currentSeqNumber = minSeqNumber;
            }

            if (currentTimeTick < lastTimeTick) {
                throw new IdGeneratorException("Time error for {} milliseconds", lastTimeTick - currentTimeTick);
            }

            lastTimeTick = currentTimeTick;

            return ((currentTimeTick << timestampShift) + (workerId << seqBitLength) + (int) currentSeqNumber);
        }
    }
}
