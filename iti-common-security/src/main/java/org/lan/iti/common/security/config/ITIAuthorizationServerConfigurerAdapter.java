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

import lombok.AllArgsConstructor;
import org.lan.iti.common.security.properties.OAuth2ClientDetailsProperties;
import org.lan.iti.common.security.properties.SecurityProperties;
import org.lan.iti.common.security.service.ITIClientDetailsService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;

import javax.sql.DataSource;

/**
 * iTi 授权服务器配置适配器
 *
 * @author NorthLan
 * @date 2020-05-23
 * @url https://noahlan.com
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
@AllArgsConstructor
@Order(90)
public class ITIAuthorizationServerConfigurerAdapter extends AuthorizationServerConfigurerAdapter {
    private final DataSource dataSource;
    private final SecurityProperties securityProperties;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients()
                .checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService());
    }

    //    @Bean
    private ClientDetailsService clientDetailsService() {
        ITIClientDetailsService clientDetailsService = new ITIClientDetailsService(dataSource);

        OAuth2ClientDetailsProperties properties = securityProperties.getClient();
        clientDetailsService.setDeleteClientDetailsSql(properties.getDeleteClientDetailsSql());
        clientDetailsService.setInsertClientDetailsSql(properties.getInsertClientDetailsSql());
        clientDetailsService.setUpdateClientDetailsSql(properties.getUpdateClientDetailsSql());
        clientDetailsService.setUpdateClientSecretSql(properties.getUpdateClientSecretSql());
        clientDetailsService.setSelectClientDetailsSql(properties.getSelectClientDetailsSql());
        clientDetailsService.setFindClientDetailsSql(properties.getFindClientDetailsSql());

        return clientDetailsService;
    }
}
