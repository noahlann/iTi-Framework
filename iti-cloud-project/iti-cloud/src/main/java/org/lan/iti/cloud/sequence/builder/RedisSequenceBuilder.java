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

package org.lan.iti.cloud.sequence.builder;

import org.lan.iti.cloud.sequence.properties.SequenceRedisProperties;
import org.lan.iti.cloud.sequence.range.Name;
import org.lan.iti.cloud.sequence.range.impl.redis.RedisRangeManager;
import org.lan.iti.cloud.sequence.sequence.Sequence;
import org.lan.iti.cloud.sequence.sequence.impl.DefaultRangeSequence;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis序列号生成器构建者
 *
 * @author NorthLan
 * @date 2020-05-06
 * @url https://noahlan.com
 */
public class RedisSequenceBuilder implements SequenceBuilder {

    /**
     * 业务名称[必选]
     */
    private Name name;

    private SequenceRedisProperties properties;
    private RedisTemplate<String, String> redisTemplate;

    public RedisSequenceBuilder properties(SequenceRedisProperties properties) {
        this.properties = properties;
        return this;
    }

    public RedisSequenceBuilder name(Name name) {
        this.name = name;
        return this;
    }

    public RedisSequenceBuilder redisTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        return this;
    }

    public static RedisSequenceBuilder create() {
        return new RedisSequenceBuilder();
    }

    @Override
    public Sequence build() {
        RedisRangeManager rangeManager = new RedisRangeManager();
        rangeManager.setRedisTemplate(this.redisTemplate)
                .setStep(this.properties.getStep())
                .setStepStart(this.properties.getStepStart())
                .init();
        DefaultRangeSequence sequence = new DefaultRangeSequence();
        sequence.setName(this.name);
        sequence.setRangeManager(rangeManager);
        return sequence;
    }
}
