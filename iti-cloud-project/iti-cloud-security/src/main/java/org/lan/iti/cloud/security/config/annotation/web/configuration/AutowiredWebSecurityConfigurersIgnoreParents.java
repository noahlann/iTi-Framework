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

import org.lan.iti.cloud.security.config.annotation.SecurityConfigurer;
import org.lan.iti.cloud.security.config.annotation.web.WebSecurityConfigurer;
import org.lan.iti.cloud.security.config.annotation.web.builders.WebSecurity;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.Assert;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 从 {@link org.springframework.context.ApplicationContext} 获取所有的 {@link WebSecurityConfigurer} 实例
 *
 * @author NorthLan
 * @date 2021/10/16
 * @url https://blog.noahlan.com
 */
public final class AutowiredWebSecurityConfigurersIgnoreParents {
    private final ConfigurableListableBeanFactory beanFactory;

    AutowiredWebSecurityConfigurersIgnoreParents(ConfigurableListableBeanFactory beanFactory) {
        Assert.notNull(beanFactory, "beanFactory cannot be null");
        this.beanFactory = beanFactory;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<SecurityConfigurer<Filter, WebSecurity>> getWebSecurityConfigurers() {
        List<SecurityConfigurer<Filter, WebSecurity>> webSecurityConfigurers = new ArrayList<>();
        Map<String, WebSecurityConfigurer> beansOfType = this.beanFactory.getBeansOfType(WebSecurityConfigurer.class);
        for (Map.Entry<String, WebSecurityConfigurer> entry : beansOfType.entrySet()) {
            webSecurityConfigurers.add(entry.getValue());
        }
        return webSecurityConfigurers;
    }
}
