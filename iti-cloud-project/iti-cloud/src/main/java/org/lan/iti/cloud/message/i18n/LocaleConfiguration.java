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

package org.lan.iti.cloud.message.i18n;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * Local配置，使用CookieLocaleResolver作为解析器
 * <p>
 * 1. 默认使用请求Headers中Accept-Language中的语言
 * 2. 若请求中包含指定cookie，则优先选取cookie所表示的语言
 * 3. 若请求中包含参数lang=xx，则进行语言切换，且设置新的cookie到浏览器中
 * <p>
 * 对于前端
 * <br>
 * 1. 浏览器首选项可以配置语言类型、优先级
 * 2. 前后端不分离时，使用后端message作为语言文件实现国际化
 * 3. 前后端分离时，自行维护国际化方式：1）获取cookie中对应的语言 2）获取浏览器语言 3）默认语言
 * 4. 切换语言时，发送任意消息附带参数 lang=xx 即可
 * <p>
 * 注：参数lang附带的是language的locale，如：zh_CN；accept-language使用的是language的tag，如：zh-Hans-CN / zh-CN
 *
 * @author NorthLan
 * @date 2021/10/20
 * @url https://blog.noahlan.com
 */
@Configuration
@EnableConfigurationProperties(I18nProperties.class)
@AllArgsConstructor
public class LocaleConfiguration implements WebMvcConfigurer {
    private final I18nProperties properties;

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public LocaleContextResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setLanguageTagCompliant(properties.isLanguageTagCompliant());
        resolver.setCookieHttpOnly(false);
        resolver.setCookieName(properties.getCookieName());
        resolver.setCookieMaxAge(properties.getCookieMaxAge());
        return resolver;
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName(properties.getParamName());
        interceptor.setHttpMethods(properties.getHttpMethods());
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
