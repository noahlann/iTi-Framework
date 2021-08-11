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

package org.lan.iti.iha.simple.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.lan.iti.iha.security.authentication.AbstractAuthenticationToken;

import java.util.Collection;

/**
 * 密码模式
 * <p>
 * 用户名密码 / 手机号密码 / 邮箱密码 / 其它+密码
 *
 * @author NorthLan
 * @date 2021/7/29
 * @url https://blog.noahlan.com
 */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@Getter
@ToString
public class SimpleAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = -7598629650069745760L;

    /**
     * principal 类型
     * username/mobile/email...etc.
     */
    private final String type;
    private final Object principal;
    private Object credentials;

    @Setter
    private String code;
    @Setter
    private String extra;
    @Setter
    private boolean rememberMe;

    public SimpleAuthenticationToken(Object principal, Object credentials, String type) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.type = type;
        setAuthenticated(false);
    }

    public SimpleAuthenticationToken(Object principal, Object credentials, String type,
                                     Collection<String> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.type = type;
        setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }
}
