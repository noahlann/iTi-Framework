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

package org.lan.iti.cloud.autoconfigure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.lan.iti.cloud.api.ApiResultWrapperAdvice;
import org.lan.iti.cloud.api.ApiResultWrapperProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ApiResult封装 自动配置
 *
 * @author NorthLan
 * @date 2021-07-22
 * @url https://noahlan.com
 */
@Configuration
@EnableConfigurationProperties(ApiResultWrapperProperties.class)
@AllArgsConstructor
public class ApiResultWrapperAdviceAutoConfiguration {
    private final ApiResultWrapperProperties properties;
    private final ObjectMapper objectMapper;

    @Bean
    public ApiResultWrapperAdvice apiResultWrapperAdvice() {
        return new ApiResultWrapperAdvice(objectMapper, properties);
    }
}
