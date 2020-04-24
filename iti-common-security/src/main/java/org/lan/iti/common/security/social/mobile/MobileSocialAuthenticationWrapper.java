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

import org.lan.iti.common.security.social.connect.ConnectionFactory;
import org.lan.iti.common.security.social.connect.support.NopConnectionFactory;
import org.lan.iti.common.security.social.mobile.connect.MobileConnectionFactory;
import org.lan.iti.common.security.social.mobile.security.MobileAuthenticationService;
import org.lan.iti.common.security.social.security.provider.SocialAuthenticationService;
import org.lan.iti.common.security.social.security.provider.SocialAuthenticationWrapper;

/**
 * 手机号连接转换器
 *
 * @author NorthLan
 * @date 2020-04-13
 * @url https://noahlan.com
 */
public class MobileSocialAuthenticationWrapper implements SocialAuthenticationWrapper {

    @Override
    public boolean support(ConnectionFactory<?> cf) {
        return cf instanceof MobileConnectionFactory;
    }

    @Override
    public <A> SocialAuthenticationService<A> wrap(ConnectionFactory<A> cf) {
        return new MobileAuthenticationService<>((NopConnectionFactory<A>) cf);
    }
}
