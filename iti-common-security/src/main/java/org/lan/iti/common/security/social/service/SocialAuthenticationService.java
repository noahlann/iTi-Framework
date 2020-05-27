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

package org.lan.iti.common.security.social.service;

import org.lan.iti.common.security.service.ITIUserDetailsService;
import org.lan.iti.common.security.social.SocialAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.TokenRequest;

import java.util.Map;

/**
 * 社交登录服务
 * {@link org.lan.iti.common.security.social.AbstractSocialTokenGranter}
 * <pre>
 *     示例:
 *     public class XXXService extends AbstractSocialTokenGranter implements SocialAuthenticationService {
 *          // ...
 *     }
 * </pre>
 *
 * @author NorthLan
 * @date 2020-05-25
 * @url https://noahlan.com
 */
public interface SocialAuthenticationService {

    /**
     * 服务提供商ID
     */
    String getProviderId();

    /**
     * 根据请求参数生成鉴权Token
     *
     * @param domain       当前请求域
     * @param tokenRequest request
     * @param parameters   参数
     */
    SocialAuthenticationToken getAuthToken(String domain, TokenRequest tokenRequest, Map<String, String> parameters);

    /**
     * 处理未登录token
     * <pre>
     *     验证手机验证码
     *     获取社交账户信息等
     * </pre>
     *
     * @param authenticationToken 上一步生成的token(经检查)
     */
    void processAuthToken(SocialAuthenticationToken authenticationToken) throws AuthenticationException;

    /**
     * 获取用户信息
     * <pre>
     *     若重写则按照重写逻辑读取，否则按照默认逻辑
     * </pre>
     *
     * @param authentication     登录token
     * @param userDetailsService 用户服务
     * @return 用户信息, 返回null则按照默认逻辑读取
     */
    default UserDetails retrieveUser(SocialAuthenticationToken authentication, ITIUserDetailsService userDetailsService) {
        return null;
    }

    /**
     * 检查用户信息
     */
    void additionalAuthenticationChecks(UserDetails userDetails, SocialAuthenticationToken authentication) throws AuthenticationException;
}
