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

package org.lan.iti.cloud.autoconfigure.sequence;

import org.lan.iti.cloud.sequence.builder.SnowflakeSequenceBuilder;
import org.lan.iti.cloud.sequence.properties.SequenceDbProperties;
import org.lan.iti.cloud.sequence.properties.SequenceRedisProperties;
import org.lan.iti.cloud.sequence.properties.SequenceSnowflakeProperties;
import org.lan.iti.cloud.sequence.sequence.Sequence;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 发号器 自动装配
 *
 * @author NorthLan
 * @date 2020-05-06
 * @url https://noahlan.com
 */
@Configuration
@EnableConfigurationProperties({
        SequenceSnowflakeProperties.class,
        SequenceDbProperties.class,
        SequenceRedisProperties.class})
@ConditionalOnProperty(prefix = "iti.sequence", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SequenceAutoConfiguration {

    /**
     * 默认实现 基于Snowflake算法的发号器实现
     *
     * @param properties 配置文件
     */
    @Bean
    @ConditionalOnMissingBean
    public Sequence snowflakeSequence(SequenceSnowflakeProperties properties) {
        return SnowflakeSequenceBuilder.create()
                .properties(properties)
                .build();
    }
}
