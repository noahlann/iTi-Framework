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
import org.lan.iti.cloud.security.exception.ITIAuth2Exception;
import org.lan.iti.cloud.security.exception.InvalidException;
import org.lan.iti.cloud.security.social.SocialAuthenticationToken;
import org.lan.iti.cloud.security.social.SocialConstants;
import org.lan.iti.cloud.security.social.service.AbstractSocialAuthenticationService;
import org.lan.iti.common.core.constants.CacheConstants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 手机号验证码 登录
 * <pre>
 * 验证码规则
 *    校验
 * 密码规则
 *    不需要校验密码
 * </pre>
 *
 * @author NorthLan
 * @date 2020-05-26
 * @url https://noahlan.com
 */
public class MobileCodeAuthenticationService extends AbstractSocialAuthenticationService {

    private final RedisTemplate<String, String> redisTemplate;

    public MobileCodeAuthenticationService(RedisTemplate<String, String> redisTemplate) {
        super(null);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getProviderId() {
        return SocialConstants.DefaultProvider.MOBILE_CODE;
    }

    @Override
    public SocialAuthenticationToken authRequest(String domain, HttpServletRequest request, Map<String, String> parameters) {
        String mobile = parameters.get("mobile");
        String code = parameters.get("code");
        if (!Validator.isMobile(mobile)) {
            throw new InvalidException("Could not authenticate user: null");
        }
        return new SocialAuthenticationToken(getProviderId(), domain, mobile, code, parameters);
    }

    @Override
    public void authenticate(SocialAuthenticationToken authRequest) throws AuthenticationException {
        String mobile = authRequest.getPrincipal().toString();
        String smsCode = authRequest.getCredentials().toString();
        String dbCode = redisTemplate.opsForValue().get(CacheConstants.MOBILE_CODE_PREFIX + mobile);
        if (StrUtil.isBlank(dbCode)) {
            throw new ITIAuth2Exception("验证码未发送或已失效");
        } else {
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
