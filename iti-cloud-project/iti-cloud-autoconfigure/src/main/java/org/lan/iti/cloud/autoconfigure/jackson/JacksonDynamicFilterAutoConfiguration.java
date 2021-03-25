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

package org.lan.iti.cloud.autoconfigure.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.lan.iti.cloud.jackson.dynamicfilter.properties.JacksonDynamicFilterProperties;
import org.lan.iti.cloud.jackson.dynamicfilter.resolver.DynamicFilterResolver;
import org.lan.iti.cloud.jackson.dynamicfilter.support.DynamicFilterResponseBodyAdvice;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 基于Jackson的动态过滤器 自动装配
 *
 * @author NorthLan
 * @date 2020-03-05
 * @url https://noahlan.com
 */
@Slf4j
@Configuration
@AutoConfigureAfter(ITIJacksonAutoConfiguration.class)
@EnableConfigurationProperties(JacksonDynamicFilterProperties.class)
@ConditionalOnProperty(value = "iti.jackson.filter.enabled", havingValue = "true", matchIfMissing = true)
@AllArgsConstructor
public class JacksonDynamicFilterAutoConfiguration implements WebMvcConfigurer {
    private final ListableBeanFactory factory;
    private final JacksonDynamicFilterProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(mappingJackson2HttpMessageConverter());
    }

    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
//        objectMapper.addMixIn(Object.class, DynamicFilterMixin.class);
//        objectMapper.setFilterProvider(new DynamicFilterProvider(null));
//        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamicFilterResponseBodyAdvice dynamicFilterResponseBodyAdvice() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        DynamicFilterResponseBodyAdvice advice = new DynamicFilterResponseBodyAdvice();
        String[] classNames = properties.getResolver().getClassNames();
        for (String name : classNames) {
            try {
                Class<?> clazz = Class.forName(name);
                advice.addResolvers((DynamicFilterResolver<?>) clazz.newInstance());
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                log.error("Failed creating a new instance of the resolver class: {}", name, e);
                if (properties.isFailFast()) {
                    throw e;
                }
            }
        }
        val resolvers = factory.getBeansOfType(DynamicFilterResolver.class);
        resolvers.values().forEach(it -> advice.addResolvers(it));
        return advice;
    }
}
