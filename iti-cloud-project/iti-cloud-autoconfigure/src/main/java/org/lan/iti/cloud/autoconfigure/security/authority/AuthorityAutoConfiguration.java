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

package org.lan.iti.cloud.autoconfigure.security.authority;

import org.lan.iti.cloud.scanner.model.ResourceDefinition;
import org.lan.iti.cloud.security.authority.ITIFilterInvocationSecurityMetadataSource;
import org.lan.iti.cloud.security.authority.service.AuthorityService;
import org.lan.iti.cloud.security.authority.service.DefaultAuthorityService;
import org.lan.iti.cloud.security.authority.vote.ITIAuthorityVoter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.oauth2.provider.expression.OAuth2WebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;

import java.util.Arrays;

/**
 * 功能权限自动装配
 *
 * @author NorthLan
 * @date 2020-05-09
 * @url https://noahlan.com
 */
@Configuration
@ConditionalOnProperty(value = "iti.authority.enabled", havingValue = "true")
@ConditionalOnClass(ResourceDefinition.class)
public class AuthorityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AuthorityService authorityService() {
        return new DefaultAuthorityService();
    }

    @Bean
    @Primary
    public ITIFilterInvocationSecurityMetadataSource itiSecurityMetadataSource() {
        return new ITIFilterInvocationSecurityMetadataSource();
    }

    @Bean
    public AccessDecisionManager accessDecisionManager() {
        WebExpressionVoter webVoter = new WebExpressionVoter();
        webVoter.setExpressionHandler(new OAuth2WebSecurityExpressionHandler());
        return new AffirmativeBased(Arrays.asList(
                new ITIAuthorityVoter(),
                webVoter,
                new RoleVoter(),
                new AuthenticatedVoter()
        ));
    }
}
