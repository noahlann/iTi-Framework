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

package org.lan.iti.cloud.autoconfigure.sentinel;

import com.alibaba.csp.sentinel.SphU;
import feign.Feign;
import org.lan.iti.cloud.sentinel.feign.FeignSentinelTarget;
import org.lan.iti.cloud.sentinel.feign.ITISentinelFeign;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.Targeter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Sentinel Feign 自动配置
 *
 * @author NorthLan
 * @date 2021-03-19
 * @url https://noahlan.com
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({SphU.class, Feign.class, ITISentinelFeign.class})
@AutoConfigureBefore(com.alibaba.cloud.sentinel.feign.SentinelFeignAutoConfiguration.class)
public class SentinelFeignAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(FeignSentinelTarget.class)
    @ConditionalOnProperty("feign.sentinel.enabled")
    protected static class SentinelFeignTargeterConfiguration {

        @Bean
        public Targeter feignTargeter() {
            return new FeignSentinelTarget();
        }
    }


    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    @ConditionalOnProperty("feign.sentinel.enabled")
    public Feign.Builder feignSentinelBuilder() {
        return ITISentinelFeign.builder();
    }
}
