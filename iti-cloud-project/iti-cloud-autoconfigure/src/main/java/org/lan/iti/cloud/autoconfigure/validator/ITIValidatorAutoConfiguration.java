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

package org.lan.iti.cloud.autoconfigure.validator;


import org.lan.iti.common.core.constants.CommonConstants;
import org.lan.iti.cloud.validate.util.Validator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;


/**
 * 数据校验器配置
 *
 * @author NorthLan
 * @date 2020-08-20
 * @url https://noahlan.com
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnClass({javax.validation.Validator.class, ValidationAutoConfiguration.class})
@AutoConfigureBefore(ValidationAutoConfiguration.class)
public class ITIValidatorAutoConfiguration {

    @Bean(name = CommonConstants.VALIDATION_MESSAGE_SOURCE_BEAN_NAME)
    @ConditionalOnMissingBean(javax.validation.Validator.class)
    public static MessageSource validationMessageSource() {
        ReloadableResourceBundleMessageSource source = new ReloadableResourceBundleMessageSource();
        source.setBasename("classpath:" + CommonConstants.DEFAULT_VALIDATOR_MESSAGES);
        source.setDefaultEncoding("UTF-8");
        return source;
    }

    /**
     * 修改默认参数验证国际化配置
     */
    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(javax.validation.Validator.class)
    public static LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
        factoryBean.setMessageInterpolator(Validator.getMessageInterpolator(validationMessageSource()));
        return factoryBean;
    }

}
