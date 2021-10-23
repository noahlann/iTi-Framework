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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 切换语言
 * <p>
 * 空接口，实际由 LocaleChangeInterceptor 切换
 * <p>
 * 需传递参数 {@link I18nProperties#getParamName()}
 *
 * @author NorthLan
 * @date 2021/10/22
 * @url https://blog.noahlan.com
 */
@RestController
@RequestMapping("/locale")
@AllArgsConstructor
public class LocaleChangeEndpoint {
    private final I18nProperties properties;

    @RequestMapping
    public String changeLocale(HttpServletRequest request) {
        return request.getParameter(properties.getParamName());
    }
}
