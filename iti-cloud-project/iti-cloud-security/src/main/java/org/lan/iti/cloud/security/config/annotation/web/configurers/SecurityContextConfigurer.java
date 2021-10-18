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

import org.lan.iti.cloud.security.config.annotation.web.HttpSecurityBuilder;
import org.lan.iti.cloud.security.config.annotation.web.builders.HttpSecurity;
import org.lan.iti.cloud.security.context.SecurityContextPersistenceFilter;
import org.lan.iti.iha.security.context.HttpSessionSecurityContextRepository;
import org.lan.iti.iha.security.context.SecurityContextRepository;

/**
 * @author NorthLan
 * @date 2021/10/15
 * @url https://blog.noahlan.com
 */
public final class SecurityContextConfigurer<H extends HttpSecurityBuilder<H>>
        extends AbstractHttpConfigurer<SecurityContextConfigurer<H>, H> {
    /**
     * Creates a new instance
     *
     * @see HttpSecurity#securityContext()
     */
    public SecurityContextConfigurer() {
    }

    /**
     * Specifies the shared {@link SecurityContextRepository} that is to be used
     *
     * @param securityContextRepository the {@link SecurityContextRepository} to use
     * @return the {@link HttpSecurity} for further customizations
     */
    public SecurityContextConfigurer<H> securityContextRepository(SecurityContextRepository securityContextRepository) {
        getBuilder().setSharedObject(SecurityContextRepository.class, securityContextRepository);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void configure(H http) {
        SecurityContextRepository securityContextRepository = http.getSharedObject(SecurityContextRepository.class);
        if (securityContextRepository == null) {
            securityContextRepository = new HttpSessionSecurityContextRepository();
        }
        SecurityContextPersistenceFilter securityContextFilter = new SecurityContextPersistenceFilter(securityContextRepository);
        // TODO SessionCreationPolicy
        securityContextFilter = postProcess(securityContextFilter);
        http.addFilter(securityContextFilter);
    }

}
