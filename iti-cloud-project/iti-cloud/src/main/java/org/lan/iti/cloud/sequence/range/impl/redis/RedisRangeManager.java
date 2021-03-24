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

package org.lan.iti.cloud.sequence.range.impl.redis;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.lan.iti.cloud.sequence.exception.SequenceException;
import org.lan.iti.cloud.sequence.range.Range;
import org.lan.iti.cloud.sequence.range.RangeManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;

/**
 * Redis 区间管理器
 *
 * @author NorthLan
 * @date 2020-05-06
 * @url https://noahlan.com
 */
public class RedisRangeManager implements RangeManager {
    /**
     * 前缀防止key重复
     */
    private final static String KEY_PREFIX = "x_sequence_";

    @Setter
    @Accessors(chain = true)
    private RedisTemplate<String, String> redisTemplate;

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
     * 标记业务key是否存在，如果false，在取nextRange时，会取check一把
     * 这个boolean只为提高性能，不用每次都取redis check
     */
    private volatile boolean keyAlreadyExist;

    @Override
    public Range nextRange(String name) throws SequenceException {
        String key = getRealKey(name);
        if (!keyAlreadyExist) {
            Boolean isExists = redisTemplate.hasKey(key);
            if (isExists == null || !isExists) {
                // 第一次不存在，进行初始化   setIfAbsent不存在就set，存在就忽略
                redisTemplate.opsForValue().setIfAbsent(key, String.valueOf(stepStart));
            }
            keyAlreadyExist = true;
        }
        Long max = redisTemplate.opsForValue().increment(key, step);
        if (max == null) {
            throw new SequenceException("redis写入数据错误，请检查连接");
        }
        long min = max - step + 1;
        return new Range(min, max);
    }

    @Override
    public void init() {
        checkParam();
    }

    private void checkParam() {
        Assert.notNull(redisTemplate, "[RedisRangeManager-redisTemplate] is null");
        if (step <= 0) {
            throw new SequenceException("[RedisRangeManager] step must greater than 0.");
        }
        if (stepStart < 0) {
            throw new SequenceException("[RedisRangeManager] stepStart < 0.");
        }
    }

    private String getRealKey(String name) {
        return KEY_PREFIX + name;
    }
}
