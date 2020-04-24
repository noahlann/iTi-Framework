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

package org.lan.iti.common.security.feign.service;

import feign.Headers;
import org.lan.iti.common.core.constants.SecurityConstants;
import org.lan.iti.common.model.response.ApiResult;
import org.lan.iti.common.security.model.SecurityUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 远程用户服务接口
 *
 * @author NorthLan
 * @date 2020-03-03
 * @url https://noahlan.com
 */
public interface RemoteUserService {

    /**
     * 通过用户ID获取用户信息
     *
     * @param userId 用户ID
     * @param domain 域
     * @return 用户安全信息
     */
    @Headers(SecurityConstants.HEADER_FROM_IN)
    @GetMapping(value = "security/user/{id}", consumes = "application/x-www-form-urlencoded")
    ApiResult<SecurityUser<?>> getSecurityUserById(@PathVariable("id") String userId,
                                                   @RequestParam("domain") String domain);

    /**
     * 通过用户名获取用户信息
     *
     * @param username 用户名
     * @param domain   域
     * @return 用户安全信息
     */
    @GetMapping(value = "security/user", consumes = "application/x-www-form-urlencoded")
    @Headers(SecurityConstants.HEADER_FROM_IN)
    ApiResult<SecurityUser<?>> getSecurityUserByUsername(@RequestParam("username") String username,
                                                         @RequestParam("domain") String domain);

    /**
     * 注册用户账户
     *
     * @param principal   唯一主体
     * @param type        类型
     * @param credentials 凭证
     * @return 用户安全信息(非完整)
     */
    @PostMapping(value = "security/user/{principal}", consumes = "application/x-www-form-urlencoded")
    @Headers(SecurityConstants.HEADER_FROM_IN)
    ApiResult<SecurityUser<?>> register(@PathVariable("principal") String principal,
                                        @RequestParam("type") String type,
                                        @RequestParam(value = "credentials", required = false) String credentials);
}
