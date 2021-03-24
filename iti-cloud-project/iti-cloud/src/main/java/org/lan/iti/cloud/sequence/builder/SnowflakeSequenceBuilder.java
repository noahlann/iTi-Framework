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

import org.lan.iti.cloud.sequence.properties.SequenceSnowflakeProperties;
import org.lan.iti.cloud.sequence.sequence.Sequence;
import org.lan.iti.cloud.sequence.sequence.impl.SnowflakeSequence;
import org.springframework.lang.NonNull;

import javax.validation.constraints.NotNull;


/**
 * 基于雪花算法，序列号生成器构建者
 *
 * @author NorthLan
 * @date 2020-05-06
 * @url https://noahlan.com
 */
public class SnowflakeSequenceBuilder implements SequenceBuilder {

    @NotNull
    private SequenceSnowflakeProperties properties;

    public static SnowflakeSequenceBuilder create() {
        return new SnowflakeSequenceBuilder();
    }

    @Override
    public Sequence build() {
        return new SnowflakeSequence(properties.getWorkerId(), properties.getDataCenterId());
    }

    // region access-chain
    public SnowflakeSequenceBuilder properties(@NonNull SequenceSnowflakeProperties properties) {
        this.properties = properties;
        return this;
    }
    // endregion
}
