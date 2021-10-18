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

package org.lan.iti.cloud.security.config.annotation.web.configurers;

import org.lan.iti.cloud.security.config.annotation.ObjectPostProcessor;
import org.lan.iti.cloud.security.config.annotation.SecurityConfigurerAdapter;
import org.lan.iti.cloud.security.config.annotation.web.HttpSecurityBuilder;
import org.lan.iti.iha.security.web.DefaultSecurityFilterChain;

/**
 * Adds a convenient base class for {@link SecurityConfigurer} instances that operate on
 * {@link HttpSecurity}.
 *
 * @author NorthLan
 * @date 2021/10/15
 * @url https://blog.noahlan.com
 */
public abstract class AbstractHttpConfigurer<T extends AbstractHttpConfigurer<T, B>, B extends HttpSecurityBuilder<B>>
        extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, B> {

    /**
     * Disables the {@link AbstractHttpConfigurer} by removing it. After doing so a fresh
     * version of the configuration can be applied.
     *
     * @return the {@link HttpSecurityBuilder} for additional customizations
     */
    @SuppressWarnings("unchecked")
    public B disable() {
        getBuilder().removeConfigurer(getClass());
        return getBuilder();
    }

    @SuppressWarnings("unchecked")
    public T withObjectPostProcessor(ObjectPostProcessor<?> objectPostProcessor) {
        addObjectPostProcessor(objectPostProcessor);
        return (T) this;
    }
}
