/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.common.feign;

import feign.Feign;
import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;
import lombok.AllArgsConstructor;
import org.lan.iti.common.core.feign.decoder.ApiResultDecoder;
import org.lan.iti.common.core.feign.decoder.ApiResultGenericRecoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.ITIFeignClientsRegistrar;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;

/**
 * Feign 自动化配置
 *
 * @author NorthLan
 * @date 2020-03-04
 * @url https://noahlan.com
 */
@Configuration
@ConditionalOnClass(Feign.class)
@Import(ITIFeignClientsRegistrar.class)
@AutoConfigureAfter(EnableFeignClients.class)
@AllArgsConstructor
public class ITIFeignAutoConfiguration {
    private final ObjectFactory<HttpMessageConverters> messageConverters;

    /**
     * 配置自定义的 Decoder
     * <p>
     * 必须实现至少一个重编码器
     * </p>
     *
     * @param genericRecodes 重编码器列表
     */
    @Bean
    public Decoder feignDecoder(List<ApiResultGenericRecoder> genericRecodes) {
        return new OptionalDecoder(
                new ResponseEntityDecoder(
                        new ApiResultDecoder(new SpringDecoder(this.messageConverters), genericRecodes)));
    }
}
