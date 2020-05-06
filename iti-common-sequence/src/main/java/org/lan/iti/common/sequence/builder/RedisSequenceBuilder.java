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

package org.lan.iti.common.sequence.builder;

import lombok.Setter;
import lombok.experimental.Accessors;
import org.lan.iti.common.sequence.range.Name;
import org.lan.iti.common.sequence.range.impl.redis.RedisRangeManager;
import org.lan.iti.common.sequence.sequence.Sequence;
import org.lan.iti.common.sequence.sequence.impl.DefaultRangeSequence;

/**
 * Redis序列号生成器构建者
 *
 * @author NorthLan
 * @date 2020-05-06
 * @url https://noahlan.com
 */
@Setter
@Accessors(chain = true)
public class RedisSequenceBuilder implements SequenceBuilder {
    /**
     * 连接redis的IP[必选]
     */
    private String ip;

    /**
     * 连接redis的port[必选]
     */
    private int port;

    /**
     * 业务名称[必选]
     */
    private Name name;

    /**
     * 认证权限，看Redis是否配置了需要密码auth[可选]
     */
    private String auth;

    /**
     * 获取range步长[可选，默认：1000]
     */
    private int step = 1000;

    /**
     * 序列号分配起始值[可选：默认：0]
     */
    private long stepStart = 0;

    public static RedisSequenceBuilder create() {
        return new RedisSequenceBuilder();
    }

    @Override
    public Sequence build() {
        RedisRangeManager rangeManager = new RedisRangeManager();
        rangeManager.setIp(this.ip)
                .setPort(this.port)
                .setAuth(this.auth)
                .setStep(this.step)
                .setStepStart(this.stepStart)
                .init();
        DefaultRangeSequence sequence = new DefaultRangeSequence();
        sequence.setName(this.name);
        sequence.setRangeManager(rangeManager);
        return sequence;
    }
}
