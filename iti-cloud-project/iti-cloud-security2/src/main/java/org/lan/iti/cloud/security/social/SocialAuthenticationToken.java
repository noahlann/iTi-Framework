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

package org.lan.iti.cloud.security.social;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

/**
 * 社交登录 Token
 *
 * @author NorthLan
 * @date 2020-05-23
 * @url https://noahlan.com
 */
public class SocialAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = -4816417936795674000L;

    /**
     * 主要信息
     * <pre>
     *     可以为 手机号/邮箱 等
     * </pre>
     */
    @Setter
    private Object principal;

    /**
     * 加密信息
     */
    @Setter
    private Object credentials;

    /**
     * 用户域
     */
    @Getter
    private final String domain;

    /**
     * 额外信息
     */
    @Getter
    private final Map<String, String> extra;

    /**
     * 服务提供商ID
     */
    @Getter
    private final String providerId;

    public SocialAuthenticationToken(String providerId,
                                     String domain,
                                     Object principal,
                                     Object credentials,
                                     Map<String, String> extra) {
        this(providerId, domain, principal, credentials, null, extra);
        setAuthenticated(false);
    }

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public SocialAuthenticationToken(String providerId,
                                     String domain,
                                     Object principal,
                                     Object credentials,
                                     Collection<? extends GrantedAuthority> authorities,
                                     Map<String, String> extra) {
        super(authorities);
        this.providerId = providerId;
        this.domain = domain;
        this.principal = principal;
        this.credentials = credentials;
        this.extra = extra;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }
}
