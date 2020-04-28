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

import feign.Contract;
import feign.Feign;
import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;
import lombok.AllArgsConstructor;
import org.lan.iti.common.core.feign.decoder.ApiResultDecoder;
import org.lan.iti.common.core.feign.decoder.ApiResultGenericRecoder;
import org.lan.iti.common.feign.properties.ITIFeignProperties;
import org.lan.iti.common.feign.spring.ITIFeignContract;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.ITIFeignClientsRegistrar;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.ConversionService;

import java.util.List;

/**
 * Feign 自动化配置
 *
 * @author NorthLan
 * @date 2020-03-04
 * @url https://noahlan.com
 */
@Configuration(proxyBeanMethods = false)
@AllArgsConstructor
@ConditionalOnClass(Feign.class)
@Import({ITIFeignClientsRegistrar.class, ITIGzipDecoderConfiguration.class})
@AutoConfigureBefore(FeignAutoConfiguration.class)
@EnableConfigurationProperties(ITIFeignProperties.class)
public class ITIFeignAutoConfiguration {
    private final ObjectFactory<HttpMessageConverters> messageConverters;

    @Autowired(required = false)
    private List<AnnotatedParameterProcessor> parameterProcessors;

    @Autowired(required = false)
    private List<ApiResultGenericRecoder> genericRecodes;

    /**
     * 配置自定义的 Decoder
     * <p>
     * 必须实现至少一个重编码器
     * </p>
     */
    @Bean
    @ConditionalOnMissingBean
    @Primary
    public Decoder feignDecoder() {
        return new OptionalDecoder(
                new ResponseEntityDecoder(
                        new ApiResultDecoder(
                                new SpringDecoder(this.messageConverters), this.genericRecodes)));
    }

    /**
     * 自定义解析器
     */
    @Bean
    public Contract feignContract(ConversionService feignConversionService) {
        return new ITIFeignContract(this.parameterProcessors, feignConversionService);
    }
}
