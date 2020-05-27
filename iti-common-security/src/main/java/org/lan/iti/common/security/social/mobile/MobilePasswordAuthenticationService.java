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

package org.lan.iti.common.security.social.mobile;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import org.lan.iti.common.core.constants.CacheConstants;
import org.lan.iti.common.security.exception.InvalidException;
import org.lan.iti.common.security.social.AbstractSocialTokenGranter;
import org.lan.iti.common.security.social.SocialAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.Map;

/**
 * 手机号密码 登录
 *
 * <pre>
 * 验证码规则
 *    累计3次密码错误
 * 密码规则
 *    校验
 * </pre>
 *
 * @author NorthLan
 * @date 2020-05-25
 * @url https://noahlan.com
 */
public class MobilePasswordAuthenticationService extends AbstractSocialTokenGranter {
    public static final String GRANT_TYPE = "mobile";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public MobilePasswordAuthenticationService(AuthenticationManager authenticationManager,
                                               AuthorizationServerTokenServices tokenServices,
                                               ClientDetailsService clientDetailsService,
                                               OAuth2RequestFactory requestFactory) {
        super(authenticationManager, tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
    }

    @Override
    public String getProviderId() {
        return GRANT_TYPE;
    }

    @Override
    public SocialAuthenticationToken getAuthToken(String domain, TokenRequest tokenRequest, Map<String, String> parameters) {
        String mobile = parameters.get("mobile");
        String password = parameters.get("password");
        if (!Validator.isMobile(mobile)) {
            throw new InvalidException("Could not authenticate user: null");
        }
        parameters.remove("mobile");
        parameters.remove("password");
        return new SocialAuthenticationToken(getGrantType(), domain, mobile, password, parameters);
    }

    @Override
    public void processAuthToken(SocialAuthenticationToken authenticationToken) {
        String mobile = authenticationToken.getPrincipal().toString();
        boolean needValidate = redisTemplate.opsForSet().isMember(CacheConstants.MOBILE_CODE_NEED_VALIDATE, mobile);
        if (needValidate) {
            String dbCode = redisTemplate.opsForValue().get(CacheConstants.MOBILE_CODE_PREFIX + mobile);
            if (StrUtil.isBlank(dbCode)) {
                // TODO throw 验证码未发送或已失效
            } else {
                String smsCode = authenticationToken.getExtra().get("code");
                if (StrUtil.isBlank(smsCode)) {
                    // 请输入验证码
                    // TODO throw
                }
                if (!StrUtil.equals(dbCode.trim(), smsCode.trim())) {
                    // TODO throw 验证码错误
                }
            }
        }
    }

    @Override
    public void additionalAuthenticationChecks(UserDetails userDetails, SocialAuthenticationToken authentication) {
        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
        String presentedPassword = authentication.getCredentials().toString();

        if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            logger.debug("Authentication failed: password does not match stored value");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
    }
}
