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

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.security.feign.RemoteUserService;
import org.lan.iti.cloud.security.model.ITIUserDetails;
import org.lan.iti.common.core.api.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
public class ITIUserDetailsServiceImpl implements ITIUserDetailsService {

    @Lazy
    @Autowired
    private RemoteUserService remoteUserService;

    @Autowired
    private UserDetailsBuilder userDetailsBuilder;

    @Override
    public ITIUserDetails loadUser(String principal, String providerId, String domain) throws UsernameNotFoundException {
        ApiResult<Object> user = remoteUserService.getSecurityUser(providerId, principal, domain);
        return buildUserDetails(user, providerId, domain);
    }

    @Override
    public ITIUserDetails register(String principal, String providerId, String domain, String credentials, Map<String, String> extra) {
        ApiResult<Object> user = remoteUserService.register(principal, providerId, credentials);
        return buildUserDetails(user, providerId, domain);
    }

    private ITIUserDetails buildUserDetails(ApiResult<Object> result, String providerId, String domain) {
        Object securityUser = result.getData();
        if (securityUser == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return userDetailsBuilder.from(securityUser, providerId, domain);
    }
}
