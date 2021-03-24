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

package org.lan.iti.cloud.security.component.pkce;

import lombok.Cleanup;
import lombok.Setter;
import org.lan.iti.common.core.constants.SecurityConstants;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStoreSerializationStrategy;

/**
 * 基于Redis的授权码生成与消费服务,默认生成32位授权码
 * <p>支持 PKCE 流程</p>
 * <p>支持 集群</p>
 *
 * @author NorthLan
 * @date 2021-03-10
 * @url https://noahlan.com
 */
public class RedisPkceAuthorizationCodeServices extends PkceRandomValueAuthorizationCodeServices {
    private final RedisConnectionFactory redisConnectionFactory;

    @Setter
    private String prefix = SecurityConstants.PREFIX + SecurityConstants.OAUTH_CODE_PREFIX;

    @Setter
    private RedisTokenStoreSerializationStrategy serializationStrategy = new JdkSerializationStrategy();

    public RedisPkceAuthorizationCodeServices(ClientDetailsService clientDetailsService,
                                              RedisConnectionFactory redisConnectionFactory) {
        super(clientDetailsService);
        this.redisConnectionFactory = redisConnectionFactory;
    }

    /**
     * 保存code与认证信息
     *
     * @param code           code
     * @param authentication Pkce-认证信息
     */
    @Override
    protected void store(String code, PkceProtectedAuthentication authentication) {
        @Cleanup
        RedisConnection connection = redisConnectionFactory.getConnection();
        connection.set(serializationStrategy.serialize(prefix + code),
                serializationStrategy.serialize(authentication));
    }

    @Override
    protected OAuth2Authentication remove(String code, String codeVerifier) {
        @Cleanup
        RedisConnection connection = redisConnectionFactory.getConnection();

        byte[] key = serializationStrategy.serialize(prefix + code);
        byte[] value = connection.get(key);

        if (value == null) {
            return null;
        }

        PkceProtectedAuthentication pkceProtectedAuthentication = serializationStrategy.deserialize(value,
                PkceProtectedAuthentication.class);
        if (pkceProtectedAuthentication == null) {
            throw new InvalidRequestException("invalid authorization_code");
        }
        // code仅允许使用一次
        connection.del(key);
        return pkceProtectedAuthentication.getOAuth2Authentication(codeVerifier);
    }
}
