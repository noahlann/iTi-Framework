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

package org.lan.iti.cloud.security.config.annotation.web;

import org.lan.iti.cloud.security.config.annotation.SecurityBuilder;
import org.lan.iti.cloud.security.config.annotation.SecurityConfigurer;

import javax.servlet.Filter;

/**
 * 允许配置 {@link org.lan.iti.cloud.security.config.annotation.web.builders.WebSecurity}
 * <p>
 * 在大多数情况下，用户将使用 {@link @EnableWebSecurity}
 * 并创建一个扩展 {@link org.lan.iti.cloud.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter} 的 {@link @Configuration}
 * 或公开一个 {@link org.lan.iti.iha.security.web.SecurityFilterChain} bean。
 * <br>
 * 两者都将通过 {@link EnableWebSecurity} 注释自动应用于 {@link org.lan.iti.cloud.security.config.annotation.web.builders.WebSecurity}。
 *
 * @author NorthLan
 * @date 2021/10/16
 * @url https://blog.noahlan.com
 */
public interface WebSecurityConfigurer<T extends SecurityBuilder<Filter>> extends SecurityConfigurer<Filter, T> {
}
