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

import org.lan.iti.cloud.security.config.annotation.web.WebSecurityConfigurer;
import org.lan.iti.cloud.security.config.annotation.web.configurers.ObjectPostProcessorConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 将此注解添加到 {@code @Configuration} 类以在任何 {@link WebSecurityConfigurer} 中定义 iTi Security 配置
 *
 * @author NorthLan
 * @date 2021/10/16
 * @url https://blog.noahlan.com
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import({ObjectPostProcessorConfiguration.class, WebSecurityConfiguration.class, HttpSecurityConfiguration.class})
@Configuration
public @interface EnableWebSecurity {
}
