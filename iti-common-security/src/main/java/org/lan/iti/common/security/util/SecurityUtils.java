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

import lombok.experimental.UtilityClass;
import org.lan.iti.common.security.model.ITIUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
     */
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 从Authentication中获取用户
     */
    public ITIUserDetails getUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof ITIUserDetails) {
            return (ITIUserDetails) principal;
        }
        return null;
    }

    /**
     * 获取当前登录用户
     */
    public ITIUserDetails getUser() {
        return getUser(getAuthentication());
    }
}
