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

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import org.lan.iti.common.core.util.Formatter;
import org.lan.iti.common.sequence.exception.SequenceException;
import org.lan.iti.common.sequence.range.Name;
import org.lan.iti.common.sequence.range.Range;
import org.lan.iti.common.sequence.range.RangeManager;
import org.lan.iti.common.sequence.sequence.RangeSequence;

import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 序列号区间生成器接口默认实现
 *
 * @author NorthLan
 * @date 2020-05-06
 * @url https://noahlan.com
 */
public class DefaultRangeSequence implements RangeSequence {
    /**
     * 获取区间是加一把独占锁防止资源冲突
     */
    private final Lock lock = new ReentrantLock();

    /**
     * 序列号区间管理器
     */
    private RangeManager rangeManager;

    /**
     * 当前序列号区间
     */
    private volatile Range currentRange;

    /**
     * 需要获取区间的名称
     */
    private Name name;

    @Override
    public void setRangeManager(RangeManager rangeManager) {
        this.rangeManager = rangeManager;
    }

    @Override
    public void setName(Name name) {
        this.name = name;
    }

    @Override
    public long next() throws SequenceException {
        String name = this.name.create();

        // 当前区间不存在，重新获取一个区间
        if (currentRange == null) {
            lock.lock();
            try {
                if (null == currentRange) {
                    currentRange = rangeManager.nextRange(name);
                }
            } finally {
                lock.unlock();
            }
        }
        // 当value值为-1时，表明区间的序列号已经分配完，需要重新获取区间
        long value = currentRange.getAndIncrement();
        if (value == -1) {
            lock.lock();
            try {
                for (; ; ) {
                    if (currentRange.isOver()) {
                        currentRange = rangeManager.nextRange(name);
                    }
                    // 可能会死循环,需要一个重试机制
                    value = currentRange.getAndIncrement();
                    if (value == -1) {
                        continue;
                    }
                    break;
                }
            } finally {
                lock.unlock();
            }
        }
        if (value < 0) {
            throw new SequenceException(Formatter.format("序列号溢出 [{}]", value));
        }
        return value;
    }

    @Override
    public String nextStr() throws SequenceException {
        return String.format("%s%05d", DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT), next());
    }

    @Override
    public String nextStr(String format) throws SequenceException {
        return String.format(format, next());
    }
}
