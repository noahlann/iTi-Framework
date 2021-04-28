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

package org.lan.iti.common.core.util.idgen;

import org.lan.iti.common.core.util.idgen.exception.IdGeneratorException;
import org.lan.iti.common.core.util.idgen.snowflake.ShiftedSnowflake;
import org.lan.iti.common.core.util.idgen.snowflake.Snowflake;
import org.lan.iti.common.core.util.idgen.snowflake.SnowflakeOptions;

/**
 * @author NorthLan
 * @date 2021-04-27
 * @url https://noahlan.com
 */
public class DefaultIdGenerator implements IGenerator {
    private IGenerator delegate = null;

    public DefaultIdGenerator(SnowflakeOptions options) throws IdGeneratorException {
        if (options == null) {
            options = new SnowflakeOptions();
        }

        // baseTime
        if (options.getBaseTime() < 315504000000L || options.getBaseTime() > System.currentTimeMillis()) {
            throw new IdGeneratorException("BaseTime error.");
        }

        // workerIdBitLength
        if (options.getWorkerIdBitLength() < 1 || options.getWorkerIdBitLength() > 21) {
            throw new IdGeneratorException("WorkerIdBitLength error.(range:[1, 21])");
        }

        //
        if (options.getWorkerIdBitLength() + options.getSeqBitLength() > 22) {
            throw new IdGeneratorException("error：WorkerIdBitLength + SeqBitLength <= 22");
        }

        // workerId
        int maxWorkerIdNumber = (1 << options.getWorkerIdBitLength()) - 1;
        if (maxWorkerIdNumber == 0) {
            maxWorkerIdNumber = 63;
        }
        if (options.getWorkerId() < 0 || options.getWorkerId() > maxWorkerIdNumber) {
            throw new IdGeneratorException("WorkerId error. (range:[0, " + (maxWorkerIdNumber > 0 ? maxWorkerIdNumber : 63) + "]");
        }

        // seqBitLength
        if (options.getSeqBitLength() < 2 || options.getSeqBitLength() > 21) {
            throw new IdGeneratorException("SeqBitLength error. (range:[2, 21])");
        }

        // maxSeqNumber
        int maxSeqNumber = (1 << options.getSeqBitLength()) - 1;
        if (maxSeqNumber == 0) {
            maxSeqNumber = 63;
        }
        if (options.getMaxSeqNumber() < 0 || options.getMaxSeqNumber() > maxSeqNumber) {
            throw new IdGeneratorException("MaxSeqNumber error. (range:[1, " + maxSeqNumber + "]");
        }

        // MinSeqNumber
        if (options.getMinSeqNumber() < 5 || options.getMinSeqNumber() > maxSeqNumber) {
            throw new IdGeneratorException("MinSeqNumber error. (range:[5, " + maxSeqNumber + "]");
        }

        // delegated
        if (options.getMethod() == SnowflakeOptions.Method.SHIFT) {
            this.delegate = new ShiftedSnowflake(options);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            this.delegate = new Snowflake(options);
        }
    }

    @Override
    public long nextLong() throws IdGeneratorException {
        return delegate.nextLong();
    }

    @Override
    public String nextStr(String format) throws IdGeneratorException {
        return delegate.nextStr(format);
    }

    @Override
    public String nextStr() throws IdGeneratorException {
        return delegate.nextStr();
    }
}
