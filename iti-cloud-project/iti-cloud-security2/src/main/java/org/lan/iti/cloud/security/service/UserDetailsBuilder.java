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

package org.lan.iti.cloud.security.service;

import org.lan.iti.cloud.security.model.ITIUserDetails;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

/**
 * 鉴权用户构建
 *
 * @author NorthLan
 * @date 2020-03-03
 * @url https://noahlan.com
 */
public interface UserDetailsBuilder {

    /**
     * 从框架定义SecurityUser构建
     *
     * @param user 安全用户信息
     */
    ITIUserDetails from(Object user, String providerId, String domain);

    /**
     * 从Authentication构建
     *
     * @param user        Authentication的Map
     * @param authorities 用户权限列表
     */
    ITIUserDetails from(Map<String, ?> user, Collection<? extends GrantedAuthority> authorities);
}
