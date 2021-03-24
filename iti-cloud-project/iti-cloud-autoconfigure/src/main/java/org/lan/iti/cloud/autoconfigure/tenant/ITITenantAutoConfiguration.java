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

package org.lan.iti.cloud.autoconfigure.tenant;

import feign.Feign;
import feign.RequestInterceptor;
import org.lan.iti.cloud.tenant.TenantClientHttpRequestInterceptor;
import org.lan.iti.cloud.tenant.TenantFeignInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;

/**
 * 框架多租户自动装配
 *
 * @author NorthLan
 * @date 2021-03-10
 * @url https://noahlan.com
 */
@Configuration
public class ITITenantAutoConfiguration {

    @Configuration
    @ConditionalOnClass(Feign.class)
    public class TenantFeignAutoConfiguration {

        @Bean
        public RequestInterceptor itiTenantFeignInterceptor() {
            return new TenantFeignInterceptor();
        }
    }

    @Bean
    public ClientHttpRequestInterceptor itiTenantRequestInterceptor() {
        return new TenantClientHttpRequestInterceptor();
    }
}
