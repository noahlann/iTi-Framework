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

import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;
import lombok.AllArgsConstructor;
import org.lan.iti.common.core.feign.decoder.ApiResultDecoder;
import org.lan.iti.common.core.feign.decoder.ApiResultGenericRecoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.DefaultGzipDecoder;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * Configures Default Gzip Decoder.
 *
 * @author NorthLan
 * @date 2020-04-27
 * @url https://noahlan.com
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty("feign.compression.response.enabled")
// The OK HTTP client uses "transparent" compression.
// If the accept-encoding header is present, it disables transparent compression
@ConditionalOnMissingBean(type = "okhttp3.OkHttpClient")
@AutoConfigureAfter(ITIFeignAutoConfiguration.class)
@AllArgsConstructor
public class ITIGzipDecoderConfiguration {
    private ObjectFactory<HttpMessageConverters> messageConverters;

    @Autowired(required = false)
    private List<ApiResultGenericRecoder> genericRecodes;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty("feign.compression.response.useGzipDecoder")
    @Primary
    public Decoder defaultGzipDecoder() {
        return new OptionalDecoder(
                new ResponseEntityDecoder(
                        new DefaultGzipDecoder(
                                new ApiResultDecoder(
                                        new SpringDecoder(this.messageConverters), this.genericRecodes))));
    }
}
