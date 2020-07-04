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

package org.lan.iti.common.feign.config;

import feign.RequestInterceptor;
import org.lan.iti.common.feign.properties.ITIFeignProperties;
import org.lan.iti.common.feign.interceptor.ITIFeignInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign租户增强
 *
 * @author NorthLan
 * @date 2020-05-15
 * @url https://noahlan.com
 */
@Configuration
@ConditionalOnProperty(prefix = ITIFeignProperties.PREFIX, name = "enabled")
public class ITIFeignTenantAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RequestInterceptor itiFeignTenantInterceptor() {
        return new ITIFeignInterceptor();
    }
}
