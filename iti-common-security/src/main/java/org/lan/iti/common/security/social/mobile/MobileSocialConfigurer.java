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

package org.lan.iti.common.security.social.mobile;

import lombok.AllArgsConstructor;
import org.lan.iti.common.security.social.config.ConnectionFactoryConfigurer;
import org.lan.iti.common.security.social.config.SocialConfigurer;
import org.lan.iti.common.security.social.connect.ConnectionFactoryLocator;
import org.lan.iti.common.security.social.connect.UsersConnectionRepository;
import org.lan.iti.common.security.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.lan.iti.common.security.social.mobile.connect.MobileConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import javax.sql.DataSource;

/**
 * 手机号社交支持 配置
 *
 * @author NorthLan
 * @date 2020-04-02
 * @url https://noahlan.com
 */
@ConditionalOnBean(DataSource.class)
@AllArgsConstructor
public class MobileSocialConfigurer implements SocialConfigurer {
    private final TextEncryptor textEncryptor;
    private final DataSource dataSource;

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer connectionFactoryConfigurer) {
        connectionFactoryConfigurer.addWrapper(new MobileSocialAuthenticationWrapper());
        connectionFactoryConfigurer.addConnectionFactory(new MobileConnectionFactory());
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        return new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, textEncryptor);
    }
}
