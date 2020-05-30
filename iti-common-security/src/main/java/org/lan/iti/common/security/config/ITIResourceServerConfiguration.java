/*
 *
 *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.common.security.config;

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.security.authority.ITIFilterInvocationSecurityMetadataSource;
import org.lan.iti.common.security.authority.config.AuthorityAutoConfiguration;
import org.lan.iti.common.security.authority.properties.AuthorityProperties;
import org.lan.iti.common.security.component.ITIUserAuthenticationConverter;
import org.lan.iti.common.security.component.ITIWebResponseExceptionTranslator;
import org.lan.iti.common.security.component.PermitAllUrlProperties;
import org.lan.iti.common.security.service.UserDetailsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.web.client.RestTemplate;

/**
 * 资源服务器配置
 * <p>
 * 1. 支持remoteTokenServices 负载均衡
 * 2. 支持 获取用户全部信息
 *
 * @author NorthLan
 * @date 2020-02-24
 * @url https://noahlan.com
 */
@Slf4j
@EnableConfigurationProperties(AuthorityProperties.class)
@Import(AuthorityAutoConfiguration.class)
public class ITIResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    @Autowired
    private RemoteTokenServices remoteTokenServices;

    @Value("${security.oauth2.resource.loadBalanced:false}")
    private Boolean oauth2ResourceLoadBalanced;

    @Autowired
    private PermitAllUrlProperties permitAllUrlProperties;

    @Autowired
    private RestTemplate lbRestTemplate;

    @Autowired
    private UserDetailsBuilder userDetailsBuilder;

    // Authority
    @Autowired(required = false)
    private AuthorityProperties authorityProperties;
    @Autowired(required = false)
    private ITIFilterInvocationSecurityMetadataSource itiSecurityMetadataSource;
    @Autowired(required = false)
    private AccessDecisionManager accessDecisionManager;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        // 允许使用iframe 嵌套，避免 swagger-ui 不被加载的问题
        http.headers().frameOptions().disable();
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorizeRequests = http.authorizeRequests();
        permitAllUrlProperties.getIgnoreUrls()
                .forEach(url -> authorizeRequests.antMatchers(url).permitAll());
        if (authorityProperties.isEnabled()) {
            authorizeRequests.withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                @Override
                public <O extends FilterSecurityInterceptor> O postProcess(O o) {
                    itiSecurityMetadataSource.setSuperMetadataSource(o.getSecurityMetadataSource());
                    o.setSecurityMetadataSource(itiSecurityMetadataSource);
                    o.setAccessDecisionManager(accessDecisionManager);
                    return o;
                }
            });
        }
        authorizeRequests.anyRequest().authenticated()
                .and().csrf().disable();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(new ITIUserAuthenticationConverter(userDetailsBuilder));

        // RemoteTokenService
        if (oauth2ResourceLoadBalanced) {
            remoteTokenServices.setRestTemplate(lbRestTemplate);
        }
        remoteTokenServices.setAccessTokenConverter(accessTokenConverter);

        // WebResponseExceptionTranslator
        WebResponseExceptionTranslator<?> webResponseExceptionTranslator = new ITIWebResponseExceptionTranslator();

        // ResourceAuthExceptionEntryPoint
        OAuth2AuthenticationEntryPoint oAuth2AuthenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
        oAuth2AuthenticationEntryPoint.setExceptionTranslator(webResponseExceptionTranslator);

        // AccessDeniedHandler
        OAuth2AccessDeniedHandler accessDeniedHandler = new OAuth2AccessDeniedHandler();
        accessDeniedHandler.setExceptionTranslator(webResponseExceptionTranslator);

        resources.authenticationEntryPoint(oAuth2AuthenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
                .tokenServices(remoteTokenServices);
    }
}
