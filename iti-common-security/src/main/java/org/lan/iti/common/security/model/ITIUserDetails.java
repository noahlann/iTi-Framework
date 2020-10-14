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

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.lang.NonNull;
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
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ITIUserDetails implements UserDetails {
    private static final long serialVersionUID = -2522947416797109973L;

    /**
     * 用户唯一ID
     */
    private String userId;

    /**
     * 认证域,适用于区分多用户源,多认证中心域
     */
    private String domain;

    /**
     * 提供商ID(mobile/wechat/qq/wechat-mini-app. etc..)
     */
    @NonNull
    private String providerId;

    /**
     * 提供商用户ID(通常openId)
     */
    private String providerUserId;

    /**
     * 凭证(用户名)
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 用户附加属性
     */
    private Map<String, Object> attributes;

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
     * 加密
     */
    public void encryptSecret() {
        if (StrUtil.isNotBlank(this.password)) {
            this.password = "Password: [PROTECTED]";
        }
    }
}
