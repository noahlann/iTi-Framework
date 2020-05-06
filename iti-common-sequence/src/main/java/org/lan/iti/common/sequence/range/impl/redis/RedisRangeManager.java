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

package org.lan.iti.common.sequence.range.impl.redis;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.lan.iti.common.sequence.exception.SequenceException;
import org.lan.iti.common.sequence.range.Range;
import org.lan.iti.common.sequence.range.RangeManager;
import redis.clients.jedis.Jedis;

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

    /**
     * redis客户端
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    private Jedis jedis;

    /**
     * IP
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    private String ip;
    /**
     * PORT
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    private Integer port;

    /**
     * 验证权限
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    private String auth;

    /**
     * 区间步长
     */
    @Getter
    @Setter
    @Accessors(chain = true)
    private int step = 1000;

    /**
     * 区间起始位置，真实从stepStart+1开始
     */
    @Getter
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
        if (!keyAlreadyExist) {
            Boolean isExists = jedis.exists(getRealKey(name));
            if (!isExists) {
                // 第一次不存在，进行初始化   setnx不存在就set，存在就忽略
                jedis.setnx(getRealKey(name), String.valueOf(stepStart));
            }
            keyAlreadyExist = true;
        }
        Long max = jedis.incrBy(getRealKey(name), step);
        long min = max - step + 1;
        return new Range(min, max);
    }

    @Override
    public void init() {
        checkParam();
        jedis = new Jedis(ip, port);
        if (StrUtil.isNotBlank(auth)) {
            jedis.auth(auth);
        }
    }

    private void checkParam() {
        if (StrUtil.isNotBlank(ip)) {
            throw new IllegalArgumentException("[RedisRangeManager-checkParam] ip is empty.");
        }
        if (null == port) {
            throw new IllegalArgumentException("[RedisRangeManager-checkParam] port is null.");
        }
    }

    private String getRealKey(String name) {
        return KEY_PREFIX + name;
    }
}
