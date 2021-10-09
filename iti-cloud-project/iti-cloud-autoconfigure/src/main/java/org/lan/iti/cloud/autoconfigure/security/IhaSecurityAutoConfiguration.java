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

package org.lan.iti.cloud.autoconfigure.security;

import org.lan.iti.cloud.security.service.DefaultUserDetailsServiceImpl;
import org.lan.iti.iha.security.IhaSecurity;
import org.lan.iti.iha.security.context.IhaSecurityContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

/**
 * Iha-Security自动装配
 *
 * @author NorthLan
 * @date 2021/9/28
 * @url https://blog.noahlan.com
 */
@Configuration
@ConditionalOnClass(IhaSecurity.class)
@AutoConfigureAfter(SecurityAutoConfiguration.class)
public class IhaSecurityAutoConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        // default user details
        IhaSecurity.init(new IhaSecurityContext()
                .setUserDetailsService(new DefaultUserDetailsServiceImpl()));
    }
}
