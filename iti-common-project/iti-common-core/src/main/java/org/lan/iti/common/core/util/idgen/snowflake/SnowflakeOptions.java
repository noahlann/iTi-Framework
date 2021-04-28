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

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 雪花算法参数
 *
 * @author NorthLan
 * @date 2021-04-27
 * @url https://noahlan.com
 */
@Data
@Accessors(chain = true)
public class SnowflakeOptions {
    /**
     * 雪花计算方法
     */
    private Method method = Method.SHIFT;

    /**
     * 基础时间（ms单位）
     * 不能超过当前系统时间
     */
    private long baseTime = 1582136402000L;

    /**
     * 机器码
     * 必须由外部设定，最大值 2^workerIdBitLength-1
     */
    private long workerId = 1;

    /**
     * 机器码位长
     * 默认值6，取值范围 [1, 15]（要求：序列数位长+机器码位长不超过22）
     */
    private byte workerIdBitLength = 6;

    /**
     * 序列数位长
     * 默认值6，取值范围 [3, 21]（要求：序列数位长+机器码位长不超过22）
     */
    private byte seqBitLength = 6;

    /**
     * 最大序列数（含）
     * 设置范围 [minSeqNumber, 2^seqBitLength-1]，默认值0，表示最大序列数取最大值（2^seqBitLength-1]）
     */
    private short maxSeqNumber = 0;

    /**
     * 最小序列数（含）
     * 默认值5，取值范围 [5, maxSeqNumber]，每毫秒的前5个序列数对应编号是0-4是保留位，其中1-4是时间回拨相应预留位，0是手工新值预留位
     */
    private short minSeqNumber = 5;

    /**
     * 最大漂移次数（含）
     * 默认5000，推荐范围500-10000（与计算能力有关）
     */
    private short topOverCostCount = 5000;

    /**
     * 是否使用优化后的系统时钟
     */
    private boolean useSystemClock = true;

    public long getBaseTime() {
        return this.baseTime != 0 ? this.baseTime : System.currentTimeMillis();
    }

    public enum Method {
        /**
         * 传统算法
         */
        COMMON,

        /**
         * 雪花漂移算法
         */
        SHIFT
    }
}
