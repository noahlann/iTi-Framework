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

package org.lan.iti.cloud.security.config.annotation.web.configuration;

import org.lan.iti.cloud.security.config.annotation.ObjectPostProcessor;
import org.lan.iti.cloud.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.Map;

import static org.lan.iti.cloud.security.config.Customizer.withDefaults;

/**
 * 配置 HttpSecurity
 *
 * @author NorthLan
 * @date 2021/10/16
 * @url https://blog.noahlan.com
 */
@Configuration(proxyBeanMethods = false)
public class HttpSecurityConfiguration {
    private static final String BEAN_NAME_PREFIX = "org.lan.iti.cloud.security.config.annotation.web.configuration.HttpSecurityConfiguration.";
    private static final String HTTPSECURITY_BEAN_NAME = BEAN_NAME_PREFIX + "httpSecurity";

    private ObjectPostProcessor<Object> objectPostProcessor;
    private ApplicationContext context;

    @Autowired
    void setObjectPostProcessor(ObjectPostProcessor<Object> objectPostProcessor) {
        this.objectPostProcessor = objectPostProcessor;
    }

    @Autowired
    void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    @Bean(HTTPSECURITY_BEAN_NAME)
    @Scope("prototype")
    HttpSecurity httpSecurity() throws Exception {
        HttpSecurity http = new HttpSecurity(this.objectPostProcessor, createSharedObjects());
        http
                .securityContext(withDefaults());
        return http;
    }

    private Map<Class<?>, Object> createSharedObjects() {
        Map<Class<?>, Object> sharedObjects = new HashMap<>();
        sharedObjects.put(ApplicationContext.class, this.context);
        return sharedObjects;
    }
}
