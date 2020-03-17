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

package org.lan.iti.common.security.social;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

/**
 * 用于社交身份验证的身份验证令牌，例如QQ、Wechat、Weibo或Gitee
 *
 * @author NorthLan
 * @date 2020-03-17
 * @url https://noahlan.com
 */
public class SocialAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 5771733949969123629L;

    private final String providerId;

    private final Object principle;

    private final Map<String, String> providerAccountData;

//    public SocialAuthenticationToken()

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
