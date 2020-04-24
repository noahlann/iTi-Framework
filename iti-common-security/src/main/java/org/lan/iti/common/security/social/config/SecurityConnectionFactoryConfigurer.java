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

package org.lan.iti.common.security.social.config;

import org.lan.iti.common.security.social.connect.ConnectionFactory;
import org.lan.iti.common.security.social.connect.support.ConnectionFactoryRegistry;
import org.lan.iti.common.security.social.security.SocialAuthenticationServiceRegistry;
import org.lan.iti.common.security.social.security.provider.OAuth2SocialAuthenticationWrapper;
import org.lan.iti.common.security.social.security.provider.SocialAuthenticationWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 安全连接工厂配置
 *
 * @author NorthLan
 * @date 2020-03-28
 * @url https://noahlan.com
 */
public class SecurityConnectionFactoryConfigurer implements ConnectionFactoryConfigurer {
    private SocialAuthenticationServiceRegistry registry;

    private List<SocialAuthenticationWrapper> wrappers;

    public SecurityConnectionFactoryConfigurer() {
        this.registry = new SocialAuthenticationServiceRegistry();
        this.wrappers = new ArrayList<>(Collections.singletonList(new OAuth2SocialAuthenticationWrapper()));
    }

    @Override
    public void addConnectionFactory(ConnectionFactory<?> connectionFactory) {
        for (SocialAuthenticationWrapper wrapper : wrappers) {
            if (wrapper.support(connectionFactory)) {
                registry.addAuthenticationService(wrapper.wrap(connectionFactory));
                break;
            }
        }
    }

    @Override
    public void addWrapper(SocialAuthenticationWrapper wrapper) {
        this.wrappers.add(wrapper);
    }

    @Override
    public ConnectionFactoryRegistry getConnectionFactoryLocator() {
        return registry;
    }
}
