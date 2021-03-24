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

package org.lan.iti.cloud.autoconfigure.security;

import cn.hutool.core.util.ReflectUtil;
import lombok.RequiredArgsConstructor;
import org.lan.iti.cloud.support.DomainContextHolder;
import org.lan.iti.cloud.tenant.TenantContextHolder;
import org.lan.iti.common.core.constants.SecurityConstants;
import org.lan.iti.common.core.util.Formatter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cglib.proxy.Proxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * Redis Token store
 *
 * @author NorthLan
 * @date 2021-03-20
 * @url https://noahlan.com
 */
@Configuration
@ConditionalOnClass({RedisConnectionFactory.class, TokenStore.class})
@RequiredArgsConstructor
public class SecurityTokenStoreAutoConfiguration {
    private final RedisConnectionFactory redisConnectionFactory;

    @Bean
    public TokenStore tokenStore() {
        // 使用jdk动态代理,修改私有变量prefix,扩展redis存储的多domain与多租户
        RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory);
        return (TokenStore) Proxy.newProxyInstance(RedisTokenStore.class.getClassLoader(),
                new Class[]{TokenStore.class},
                (o, method, objects) -> {
                    // 修改私有变量 prefix
                    // tenantId:domain:iti_oauth:
                    ReflectUtil.setFieldValue(redisTokenStore, "prefix",
                            Formatter.format("{}:{}:{}",
                                    TenantContextHolder.getTenantId(),
                                    DomainContextHolder.getDomain(),
                                    SecurityConstants.PREFIX + SecurityConstants.OAUTH_PREFIX));
                    return method.invoke(redisTokenStore, objects);
                });
    }
}
