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

package org.lan.iti.common.sequence.range;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 序列号区间对象模型
 *
 * @author NorthLan
 * @date 2020-05-06
 * @url https://noahlan.com
 */
@Getter
@ToString
public class Range {
    /**
     * 区间的序列号开始值
     */
    private final long min;

    /**
     * 区间的序列号结束值
     */
    private final long max;

    /**
     * 区间的序列号当前值
     */
    @Getter(AccessLevel.NONE)
    private final AtomicLong value;

    /**
     * 区间的序列号是否分配完毕，每次分配完毕就会去重新获取一个新的区间
     */
    @Setter
    private volatile boolean over = false;

    public Range(long min, long max) {
        this.min = min;
        this.max = max;
        this.value = new AtomicLong(min);
    }

    /**
     * 返回并递增下一个序列号
     *
     * @return 下一个序列号，如果返回-1表示序列号分配完毕
     */
    public long getAndIncrement() {
        long currentValue = value.getAndIncrement();
        if (currentValue > max) {
            over = true;
            return -1;
        }
        return currentValue;
    }
}
