/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.common.security.model;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Map;

/**
 * 扩展 Security 用户模型
 *
 * @author NorthLan
 * @date 2020-02-25
 * @url https://noahlan.com
 */
@Builder
@Accessors(chain = true)
public class ITIUser implements UserDetails {
    private static final long serialVersionUID = -2522947416797109973L;

    /**
     * 用户唯一ID
     */
    @Getter
    private String uid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户权限
     */
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * 是否已锁定
     */
    private boolean accountNonLocked;

    /**
     * 是否已过期
     */
    private boolean accountNonExpired;

    /**
     * 是否启用
     */
    private boolean enabled;

    /**
     * 密码是否已过期
     */
    private boolean credentialsNonExpired;

    /**
     * 认证客户端ID
     */
    @Getter
    private String clientId;

    /**
     * 认证中心域,适用于区分多用户源,多认证中心域
     */
    @Getter
    private String domain;

    /**
     * 昵称
     */
    @Getter
    private String nickName;

    /**
     * 头像
     */
    @Getter
    private String avatar;

    /**
     * 账户Id
     */
    @Getter
    private String accountId;

    /***
     * 账户类型
     */
    @Getter
    private String accountType;

    /**
     * 用户附加属性
     */
    @Getter
    private Map<String, Object> attributes;

    /**
     * 租户ID
     */
    @Getter
    private String tenantId;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
