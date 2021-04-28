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

import org.lan.iti.common.core.util.SystemClock;
import org.lan.iti.common.core.util.idgen.IGenerator;
import org.lan.iti.common.core.util.idgen.exception.IdGeneratorException;

/**
 * 雪花ID生成器
 *
 * @author NorthLan
 * @date 2021-04-27
 * @url https://noahlan.com
 */
public class Snowflake implements IGenerator {
    /**
     * 基础时间
     */
    protected final long baseTime;

    /**
     * 机器码
     */
    protected final long workerId;

    /**
     * 机器码位长
     */
    protected final byte workerIdBitLength;

    /**
     * 自增序列数位长
     */
    protected final byte seqBitLength;

    /**
     * 最大序列数（含）
     */
    protected final int maxSeqNumber;

    /**
     * 最小序列数（含）
     */
    protected final short minSeqNumber;

    /**
     * 最大漂移次数（含）
     */
    protected final int topOverCostCount;

    /**
     * 是否使用优化后的系统时钟
     */
    protected final boolean useSystemClock;


    protected final byte timestampShift;
    protected final static byte[] syncLock = new byte[0];

    protected short currentSeqNumber;
    protected long lastTimeTick = 0;
    protected long turnBackTimeTick = 0;
    protected byte turnBackIndex = 0;

    protected boolean overCost = false;
    protected int overCostCountInOneTerm = 0;
    protected int genCountInOneTerm = 0;
    protected int termIndex = 0;

    public Snowflake() {
        this(new SnowflakeOptions());
    }

    public Snowflake(SnowflakeOptions options) {
        this.baseTime = options.getBaseTime();
        this.workerIdBitLength = options.getWorkerIdBitLength();
        this.workerId = options.getWorkerId();
        this.seqBitLength = options.getSeqBitLength();
        this.maxSeqNumber = options.getMaxSeqNumber();
        this.useSystemClock = options.isUseSystemClock();
        this.minSeqNumber = options.getMinSeqNumber();
        this.topOverCostCount = options.getTopOverCostCount();
        //
        this.timestampShift = (byte) (workerIdBitLength + seqBitLength);
        this.currentSeqNumber = minSeqNumber;
    }

    private void endOverCostAction(long timeTick) {
        if (this.termIndex > 10000) {
            this.termIndex = 0;
        }
    }

    private long nextOverCostId() {
        // 漂移
        long currentTimeTick = getCurrentTimeTick();

        if (currentTimeTick > this.lastTimeTick) {
            endOverCostAction(currentTimeTick);

            this.lastTimeTick = currentTimeTick;
            this.currentSeqNumber = minSeqNumber;
            this.overCost = false;
            this.overCostCountInOneTerm = 0;
            this.genCountInOneTerm = 0;

            return calc(this.lastTimeTick);
        }

        if (this.overCostCountInOneTerm >= this.topOverCostCount) {
            endOverCostAction(currentTimeTick);

            this.lastTimeTick = getNextTimeTick();
            this.currentSeqNumber = minSeqNumber;
            this.overCost = false;
            this.overCostCountInOneTerm = 0;
            this.genCountInOneTerm = 0;

            return calc(lastTimeTick);
        }

        if (currentSeqNumber > maxSeqNumber) {
            this.lastTimeTick++;
            this.currentSeqNumber = minSeqNumber;
            this.overCost = true;
            this.overCostCountInOneTerm++;
            this.genCountInOneTerm++;

            return calc(lastTimeTick);
        }

        return calc(lastTimeTick);
    }

    private long nextNormalId() {
        long currentTimeTick = getCurrentTimeTick();

        // 时钟回拨 闰秒问题
        if (currentTimeTick < this.lastTimeTick) {
            if (this.turnBackTimeTick < 1) {
                this.turnBackTimeTick = this.lastTimeTick - 1;
                this.turnBackIndex++;

                // 每毫秒序列数的前5位是预留位，0用于手工新值，1-4是时间回拨次序
                // 支持4次回拨次序（避免回拨重叠导致ID重复），可无限次回拨（次序循环使用）
                if (this.turnBackIndex > 4) {
                    this.turnBackIndex = 1;
                }
            }
            return calcTurnBackId(this.turnBackTimeTick);
        }

        // 时间追平时，turnBackIndex 清零
        if (this.turnBackTimeTick > 0) {
            this.turnBackTimeTick = 0;
        }

        if (currentTimeTick > lastTimeTick) {
            this.lastTimeTick = currentTimeTick;
            this.currentSeqNumber = minSeqNumber;
            return calc(lastTimeTick);
        }

        if (currentSeqNumber > maxSeqNumber) {
            this.termIndex++;
            this.lastTimeTick++;
            this.currentSeqNumber = minSeqNumber;
            this.overCost = true;
            this.overCostCountInOneTerm = 1;
            this.genCountInOneTerm = 1;

            return calc(this.lastTimeTick);
        }
        return calc(this.lastTimeTick);
    }

    protected long calc(long timeTick) {
        long result = ((timeTick << this.timestampShift) +
                (this.workerId << this.seqBitLength) +
                (int) this.currentSeqNumber);
        this.currentSeqNumber++;
        return result;
    }

    protected long calcTurnBackId(long timeTick) {
        long result = ((timeTick << this.timestampShift) +
                (this.workerId << this.seqBitLength) +
                this.turnBackIndex);
        this.turnBackTimeTick++;
        return result;
    }

    protected long getCurrentTimeTick() {
        long millis = this.useSystemClock ? SystemClock.now() : System.currentTimeMillis();
        return millis - this.baseTime;
    }

    protected long getNextTimeTick() {
        long tempTimeTick = getCurrentTimeTick();

        while (tempTimeTick <= lastTimeTick) {
            tempTimeTick = getCurrentTimeTick();
        }
        return tempTimeTick;
    }

    @Override
    public long nextLong() throws IdGeneratorException {
        synchronized (syncLock) {
            return overCost ? nextOverCostId() : nextNormalId();
        }
    }

    @Override
    public String nextStr(String format) throws IdGeneratorException {
        return String.format(format, this.nextLong());
    }

    @Override
    public String nextStr() throws IdGeneratorException {
        return String.valueOf(this.nextLong());
    }
}
