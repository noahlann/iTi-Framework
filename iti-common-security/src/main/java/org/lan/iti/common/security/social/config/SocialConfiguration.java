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

import org.lan.iti.common.security.social.connect.ConnectionFactoryLocator;
import org.lan.iti.common.security.social.connect.UsersConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author NorthLan
 * @date 2020-03-21
 * @url https://noahlan.com
 */
@Configuration
public class SocialConfiguration {
    private static boolean securityEnabled = isSocialSecurityAvailable();

    @Autowired
    private Environment environment;

    private List<SocialConfigurer> socialConfigurers;

    @Autowired
    public void setSocialConfigurers(List<SocialConfigurer> socialConfigurers) {
        Assert.notNull(socialConfigurers, "At least one configuration class must implement SocialConfigurer (or subclass SocialConfigurerAdapter)");
        Assert.notEmpty(socialConfigurers, "At least one configuration class must implement SocialConfigurer (or subclass SocialConfigurerAdapter)");
        this.socialConfigurers = socialConfigurers;
    }

    @Bean
    public ConnectionFactoryLocator connectionFactoryLocator() {
        if (securityEnabled) {

        } else {


        }
        return null;
    }

    @Bean
    public UsersConnectionService usersConnectionService(ConnectionFactoryLocator connectionFactoryLocator) {
        UsersConnectionService usersConnectionService = null;
        for (SocialConfigurer socialConfigurer : socialConfigurers) {
            UsersConnectionService ucsCandidate = socialConfigurer.getUsersConnectionRepository(connectionFactoryLocator);
            if (ucsCandidate != null) {
                usersConnectionService = ucsCandidate;
                break;
            }
        }
        Assert.notNull(usersConnectionService, "One configuration class must implement getUsersConnectionRepository from SocialConfigurer.");
        return usersConnectionService;
    }

//    @Bean
//    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
//    public ConnectionService connectionService(UsersConnectionService usersConnectionService){
//        return usersConnectionService.createConnectionService()
//    }

    private static boolean isSocialSecurityAvailable() {
        try {
            Class.forName("org.lan.iti.common.security.social.SocialAuthenticationServiceLocator");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
