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
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.security.model.ITIUserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;

import java.util.Collection;
import java.util.Map;

/**
 * 抽象类，默认处理
 *
 * @author NorthLan
 * @date 2020-04-17
 * @url https://noahlan.com
 */
@Slf4j
public abstract class AbstractUserDetailsBuilder implements UserDetailsBuilder {

    @Override
    public ITIUserDetails from(Object user, String providerId, String domain) {
        return this.build(user, providerId, domain)
                .setDomain(domain)
                .setProviderId(providerId)
                .setCredentialsNonExpired(true);
    }

    /**
     * 构建安全用户信息
     *
     * @param userMap    用户模型,交由子类获取
     * @param providerId 提供商ID
     * @param domain     域
     */
    protected abstract ITIUserDetails build(Object userMap, String providerId, String domain);

    @Override
    public ITIUserDetails from(Map<String, ?> user, Collection<? extends GrantedAuthority> authorities) {
        // hutools-BeanUtil无法转换GrantedAuthority,忽略后使用传入的authorities
        // 注: 资源服务器转换后将全部变为SimpleGrantedAuthority(权限过期操作在上一个from过滤)
        ITIUserDetails result = BeanUtil.toBean(user, ITIUserDetails.class, CopyOptions.create()
                .ignoreNullValue()
                .ignoreError()
                .setIgnoreProperties(AccessTokenConverter.AUTHORITIES));
        result.setAuthorities(authorities);
        return result;
    }
}
