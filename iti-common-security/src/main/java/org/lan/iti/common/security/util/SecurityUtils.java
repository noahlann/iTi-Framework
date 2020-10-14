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

package org.lan.iti.common.security.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ReflectUtil;
import lombok.experimental.UtilityClass;
import org.lan.iti.common.core.util.LambdaUtils;
import org.lan.iti.common.security.model.ITIUserDetails;
import org.lan.iti.common.security.social.SocialAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;

/**
 * 安全工具类
 *
 * @author NorthLan
 * @date 2020-04-10
 * @url https://noahlan.com
 */
@UtilityClass
public class SecurityUtils {

    /**
     * 获取Authentication
     *
     * @return Authentication
     */
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 从Authentication中获取用户
     *
     * @return iTi定义的用户信息
     */
    public Optional<ITIUserDetails> getUser(Authentication authentication) {
        ITIUserDetails details = null;
        Object principal = authentication.getPrincipal();
        if (principal instanceof ITIUserDetails) {
            details = (ITIUserDetails) principal;
        }
        return Optional.ofNullable(details);
    }

    /**
     * 获取当前登录用户
     */
    public Optional<ITIUserDetails> getUser() {
        return getUser(getAuthentication());
    }

    /**
     * 设置当前线程下的authentication对象
     *
     * @param authentication 登录信息
     */
    public void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    /**
     * 设置当前线程下的用户信息
     *
     * @param userDetails iTi定义用户安全信息
     */
    public void setUserDetails(ITIUserDetails userDetails) {
        Authentication userAuthentication = getAuthentication();
        Optional<ITIUserDetails> existingUser = getUser(userAuthentication);
        existingUser.ifPresent(it -> {
            BeanUtil.copyProperties(userDetails, it, CopyOptions.create(ITIUserDetails.class, true,
                    LambdaUtils.getFieldName(ITIUserDetails::getUserId),
                    LambdaUtils.getFieldName(ITIUserDetails::getDomain),
                    LambdaUtils.getFieldName(ITIUserDetails::getProviderId),
                    LambdaUtils.getFieldName(ITIUserDetails::getTenantId)));
            // authorities
            if (userAuthentication instanceof UsernamePasswordAuthenticationToken || userAuthentication instanceof SocialAuthenticationToken) {
                ReflectUtil.setFieldValue(userAuthentication,
                        LambdaUtils.getFieldName(Authentication::getAuthorities),
                        Collections.unmodifiableCollection(userDetails.getAuthorities()));
            }
        });
    }
}
