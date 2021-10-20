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

import cn.hutool.core.convert.Convert;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.time.Duration;

/**
 * i18n 配置
 *
 * @author NorthLan
 * @date 2021/10/20
 * @url https://blog.noahlan.com
 */
@Data
@RefreshScope
@ConfigurationProperties(prefix = I18nProperties.PREFIX)
public class I18nProperties {
    public static final String PREFIX = "iti.i18n";

    /**
     * 是否兼容 languageTag
     *
     * @see org.springframework.web.servlet.i18n.CookieLocaleResolver#setLanguageTagCompliant(boolean)
     */
    private boolean languageTagCompliant = false;

    /**
     * cookie 名称
     *
     * @see org.springframework.web.servlet.i18n.CookieLocaleResolver#setCookieName(String)
     */
    private String cookieName = "_iti:i18n";

    /**
     * cookie max-age (秒)
     *
     * @see org.springframework.web.servlet.i18n.CookieLocaleResolver#setCookieMaxAge(Integer)
     */
    private Integer cookieMaxAge = Convert.toInt(Duration.ofDays(7).getSeconds());

    /**
     * 参数名称
     *
     * @see org.springframework.web.servlet.i18n.LocaleChangeInterceptor#setParamName(String)
     */
    private String paramName = "lang";

    /**
     * 允许的请求方式
     *
     * @see org.springframework.web.servlet.i18n.LocaleChangeInterceptor#setHttpMethods(String...)
     */
    private String[] httpMethods;
}
