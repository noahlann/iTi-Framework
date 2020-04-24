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

package org.lan.iti.common.security.social.security.provider;

import org.lan.iti.common.security.social.connect.ConnectionFactory;
import org.lan.iti.common.security.social.connect.support.OAuth2ConnectionFactory;

/**
 * OAuth2 默认转换逻辑
 *
 * @author NorthLan
 * @date 2020-04-02
 * @url https://noahlan.com
 */
public class OAuth2SocialAuthenticationWrapper implements SocialAuthenticationWrapper {

    @Override
    public boolean support(ConnectionFactory<?> cf) {
        return cf instanceof OAuth2ConnectionFactory;
    }

    @Override
    public <A> SocialAuthenticationService<A> wrap(ConnectionFactory<A> cf) {
        final OAuth2AuthenticationService<A> authService = new OAuth2AuthenticationService<>((OAuth2ConnectionFactory<A>) cf);
        authService.setDefaultScope(((OAuth2ConnectionFactory<A>) cf).getScope());
        return authService;
    }
}
