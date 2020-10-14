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

package org.lan.iti.common.core.autoconfigure;

import org.lan.iti.common.core.constants.ITIConstants;
import org.lan.iti.common.core.handler.RestResponseErrorHandler;
import org.lan.iti.common.core.properties.ErrorProperties;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Map;


/**
 * iTi 配置
 *
 * @author NorthLan
 * @date 2020-04-30
 * @url https://noahlan.com
 */
@Configuration(proxyBeanMethods = false)
@Order(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(ErrorProperties.class)
public class ITIAutoConfiguration implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 默认国际化配置
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename(ITIConstants.DEFAULT_MESSAGES);
        return messageSource;
    }

    /**
     * 负载均衡的RestTemplate
     */
    @Bean
    @LoadBalanced
    public RestTemplate lbRestTemplate() {
        return itiRestTemplate();
    }

    /**
     * RestTemplate
     */
    @Bean
    @Primary
    public RestTemplate restTemplate() {
        return itiRestTemplate();
    }

    private RestTemplate itiRestTemplate() {
//        Map<String, ClientHttpRequestInterceptor> interceptorMap = applicationContext
//                .getBeansOfType(ClientHttpRequestInterceptor.class);
        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.setInterceptors(new ArrayList<>(interceptorMap.values()));
        restTemplate.setErrorHandler(new RestResponseErrorHandler());
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        return restTemplate;
    }
}
