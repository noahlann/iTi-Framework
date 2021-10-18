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

import org.lan.iti.cloud.security.config.annotation.web.builders.HttpSecurity;
import org.lan.iti.cloud.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.lan.iti.iha.security.web.SecurityFilterChain;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

/**
 * iTi-security 环境存在 且
 * 未配置 {@link WebSecurityConfigurerAdapter} 与 {@link SecurityFilterChain}
 *
 * @author NorthLan
 * @date 2021/10/16
 * @url https://blog.noahlan.com
 */
class DefaultWebSecurityCondition extends AllNestedConditions {

    DefaultWebSecurityCondition() {
        super(ConfigurationPhase.REGISTER_BEAN);
    }

    @ConditionalOnClass({SecurityFilterChain.class, HttpSecurity.class})
    static class Classes {

    }

    @ConditionalOnMissingBean({WebSecurityConfigurerAdapter.class, SecurityFilterChain.class})
    static class Beans {

    }
}
