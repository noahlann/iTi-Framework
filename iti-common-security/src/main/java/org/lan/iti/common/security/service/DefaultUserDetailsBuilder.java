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

package org.lan.iti.common.security.service;

import cn.hutool.core.bean.BeanUtil;
import org.lan.iti.common.security.model.ITIUserDetails;
import org.lan.iti.common.security.model.SecurityUser;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

/**
 * @author NorthLan
 * @date 2020-04-18
 * @url https://noahlan.com
 */
public class DefaultUserDetailsBuilder extends AbstractUserDetailsBuilder {
    @Override
    public ITIUserDetails from(SecurityUser<?> user, String providerId, String domain) {
        return super.from(user, providerId, domain);
    }

    @Override
    public ITIUserDetails from(Map<String, ?> user, Collection<? extends GrantedAuthority> authorities) {
        ITIUserDetails result = BeanUtil.mapToBean(user, ITIUserDetails.class, true);
        result.setAuthorities(authorities);
        return result;
    }
}
