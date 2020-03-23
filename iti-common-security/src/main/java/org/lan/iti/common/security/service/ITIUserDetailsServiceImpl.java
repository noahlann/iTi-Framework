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

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.model.response.ApiResult;
import org.lan.iti.common.security.feign.service.RemoteUserService;
import org.lan.iti.common.security.model.SecurityUser;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Map;

/**
 * 扩展用户信息服务接口 实现类
 *
 * @author NorthLan
 * @date 2020-02-27
 * @url https://noahlan.com
 */
@Slf4j
@AllArgsConstructor
public class ITIUserDetailsServiceImpl implements ITIUserDetailsService {
    private final CacheManager cacheManager;
    private final RemoteUserService remoteUserService;
    private final UserBuilder userBuilder;

    @Override
    public UserDetails loadUser(String principal, String providerId, String credentials, Map<String, String> extra) throws UsernameNotFoundException {
        // TODO 更多逻辑判定
        ApiResult<SecurityUser<?>> user = remoteUserService.getSecurityUser(principal, providerId);
        return buildUserDetails(user);
    }

    @Override
    public UserDetails register(String principal, String type, String credentials, Map<String, String> extra) {
        // TODO 更多逻辑判定
        ApiResult<SecurityUser<?>> user = remoteUserService.register(principal, type, credentials);
        return buildUserDetails(user);
    }

    @SneakyThrows
    private UserDetails buildUserDetails(ApiResult<SecurityUser<?>> result) {
        SecurityUser<?> securityUser = result.getData();
        if (securityUser == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return userBuilder.from(securityUser);
    }
}
