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

package org.lan.iti.cloud.autoconfigure.swagger;

import com.fasterxml.classmate.TypeResolver;
import lombok.AllArgsConstructor;
import org.lan.iti.cloud.web.page.PageableTypeRuleConvention;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.spring.web.OnServletBasedWebApplication;

/**
 * @author NorthLan
 * @date 2021-07-23
 * @url https://noahlan.com
 */
@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({OnServletBasedWebApplication.class, SpringDataWebProperties.class})
@AutoConfigureAfter(SpringDataWebAutoConfiguration.class)
@AllArgsConstructor
public class SpringfoxRuleAutoConfiguration {
    private final SpringDataWebProperties properties;

    @Bean
    @Primary
    public AlternateTypeRuleConvention pageableConvention(TypeResolver typeResolver) {
        return new PageableTypeRuleConvention(properties, typeResolver);
    }


}
