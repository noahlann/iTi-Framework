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

import com.google.common.collect.Lists;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;

/**
 * 鉴权用户信息
 *
 * @param <T> 系统账户类型，必须可序列化
 * @author NorthLan
 * @date 2020-03-02
 * @url https://noahlan.com
 */
@Data
public class SecurityUser<T> implements Serializable {
    private static final long serialVersionUID = 3904799015848436768L;

    /**
     * 用户账户 信息
     */
    private T accountInfo = null;

    /**
     * 用户 ID
     */
    private String userId;

    private String providerId;

    private String providerUserId;

    private String domain;

    /**
     * 用户权限标识
     */
    private Collection<? extends GrantedAuthority> authorities = Lists.newArrayList();

    /**
     * 用户角色代码列表
     */
    private Collection<String> roles = Lists.newArrayList();
}
