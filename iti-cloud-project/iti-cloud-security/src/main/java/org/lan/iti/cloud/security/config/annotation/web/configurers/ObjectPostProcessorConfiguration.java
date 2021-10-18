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

package org.lan.iti.cloud.security.config.annotation.web.configurers;

import org.lan.iti.cloud.security.config.annotation.ObjectPostProcessor;
import org.lan.iti.cloud.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * ObjectPostProcessor的自动配置
 * <p>
 * 使用 {@link EnableWebSecurity} 时候进行 import
 *
 * @author NorthLan
 * @date 2021/10/18
 * @url https://blog.noahlan.com
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ObjectPostProcessorConfiguration {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public ObjectPostProcessor<Object> objectPostProcessor(AutowireCapableBeanFactory beanFactory) {
        return new AutowireBeanFactoryObjectPostProcessor(beanFactory);
    }
}
