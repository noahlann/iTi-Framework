/*
 *
 *  * Copyright (c) [2019-2021] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.cloud.security.social.provider.mobile;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.security.exception.ITIAuth2Exception;
import org.lan.iti.cloud.security.exception.InvalidException;
import org.lan.iti.cloud.security.model.ITIUserDetails;
import org.lan.iti.cloud.security.social.SocialAuthenticationToken;
import org.lan.iti.cloud.security.social.SocialConstants;
import org.lan.iti.cloud.security.social.service.AbstractSocialAuthenticationService;
import org.lan.iti.common.core.constants.CacheConstants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
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
@Slf4j
public class MobilePasswordAuthenticationService extends AbstractSocialAuthenticationService {
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public MobilePasswordAuthenticationService(RedisTemplate<String, String> redisTemplate) {
        super(null);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getProviderId() {
        return SocialConstants.DefaultProvider.MOBILE_PASSWORD;
    }

    @Override
    public SocialAuthenticationToken authRequest(String domain, HttpServletRequest request, Map<String, String> parameters) {
        String mobile = request.getParameter("mobile");
        String password = request.getParameter("password");
        if (!Validator.isMobile(mobile)) {
            throw new InvalidException("Could not authenticate user: null");
        }
        return new SocialAuthenticationToken(getProviderId(), domain, mobile, password, parameters);
    }

    @Override
    public void authenticate(SocialAuthenticationToken authRequest) throws AuthenticationException {
        String mobile = authRequest.getPrincipal().toString();
        boolean needValidate = redisTemplate.opsForSet().isMember(CacheConstants.MOBILE_CODE_NEED_VALIDATE, mobile);
        if (needValidate) {
            String dbCode = redisTemplate.opsForValue().get(CacheConstants.MOBILE_CODE_PREFIX + mobile);
            if (StrUtil.isBlank(dbCode)) {
                throw new ITIAuth2Exception("验证码未发送或已失效");
            } else {
                String smsCode = authRequest.getExtra().get("code");
                if (StrUtil.isBlank(smsCode)) {
                    throw new ITIAuth2Exception("请输入验证码");
                }
                if (!StrUtil.equals(dbCode.trim(), smsCode.trim())) {
                    // TODO throw 验证码错误
                    throw new BadCredentialsException(messages.getMessage(
                            "AbstractUserDetailsAuthenticationProvider.badCredentials",
                            "Bad credentials"));
                }
            }
        }
    }

    @Override
    public void additionalAuthenticationChecks(ITIUserDetails userDetails, SocialAuthenticationToken authentication) throws AuthenticationException {
        if (authentication.getCredentials() == null) {
            log.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
        String presentedPassword = authentication.getCredentials().toString();

        if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            log.debug("Authentication failed: password does not match stored value");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials",
                    "Bad credentials"));
        }
    }


}
