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

package org.lan.iti.cloud.autoconfigure.feign;

import feign.Contract;
import feign.Feign;
import lombok.AllArgsConstructor;
import org.lan.iti.cloud.feign.ITIFeignContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;

import java.util.List;

/**
 * @author NorthLan
 * @date 2021-03-09
 * @url https://noahlan.com
 */
@Configuration
@ConditionalOnClass(Feign.class)
@ConditionalOnWebApplication
@AutoConfigureAfter(EnableFeignClients.class)
@AllArgsConstructor
public class FeignContractAutoConfiguration {

    @Autowired(required = false)
    private final List<AnnotatedParameterProcessor> parameterProcessors;

    /**
     * 自定义解析器
     */
    @Bean
    public Contract feignContract(ConversionService feignConversionService) {
        return new ITIFeignContract(this.parameterProcessors, feignConversionService);
    }
}
