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

package org.lan.iti.common.security.service;

import org.lan.iti.common.security.enums.ITIAuthType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Map;

/**
 * 扩展用户信息服务接口
 * <p>
 * 添加支持多种不同类型账户鉴权
 *
 * @author NorthLan
 * @date 2020-02-27
 * @url https://noahlan.com
 */
public interface ITIUserDetailsService extends UserDetailsService {

    /**
     * 根据 唯一标识与类型 查询 UserDetails
     *
     * @param principal   唯一标识 (手机号/微信openid/微博openid等)
     * @param providerId        鉴权类型代码
     * @param credentials 身份信息（密码等）
     * @param extra       扩展信息
     * @return 用户鉴权模型
     */
    UserDetails loadUser(String principal, String providerId, String credentials, Map<String, String> extra) throws UsernameNotFoundException;

    /**
     * 自动注册
     *
     * @param principal   唯一标识 (手机号/微信openid/微博openid等)
     * @param type        鉴权类型代码
     * @param credentials 身份信息（密码等）
     * @param extra       扩展信息
     * @return 用户鉴权模型（粗略版）
     */
    UserDetails register(String principal, String type, String credentials, Map<String, String> extra);

    /**
     * 扩展默认实现,最终通过loadUser方法载入用户信息
     */
    @Override
    default UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadUser(username, ITIAuthType.USERNAME.getType(), null, null);
    }
}
