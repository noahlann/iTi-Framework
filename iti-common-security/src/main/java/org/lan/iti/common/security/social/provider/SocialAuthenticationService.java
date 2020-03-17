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

package org.lan.iti.common.security.social.provider;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * 社交身份鉴权服务
 *
 * @author NorthLan
 * @date 2020-03-17
 * @url https://noahlan.com
 */
public interface SocialAuthenticationService<S> {
    /**
     * @return {@link ConnectionCardinality} for connections to this provider
     */
    ConnectionCardinality getConnectionCardinality();

    /**
     * Connection关系
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public enum ConnectionCardinality {
        /**
         * 一对一，每个userId仅一个连接的providerUserId，反之亦然
         */
        ONE_TO_ONE(false, false),

        /**
         * 一对多，一个userId可连接多个providerUserId，但是每个providerUserId只能连接一个userId
         */
        ONE_TO_MANY(false, true),

        /**
         * 多对一，每个userId只能有一个providerUserId，但是每个providerUserId可以有许多userId.
         * 注：无法对用户进行鉴权
         */
        MANY_TO_ONE(true, false),

        /**
         * 多对多，无限制。无法验证用户
         */
        MANY_TO_MANY(true, true);

        private final boolean multiUserId;
        private final boolean multiProviderUserId;

        /**
         * 每个providerUserId允许多个userId。如果为true，则无法进行身份验证
         *
         * @return 如果每个提供者用户标识允许多个本地用户，则为true
         */
        public boolean isMultiUserId() {
            return multiUserId;
        }

        /**
         * 每个userId允许许多providerUserId
         *
         * @return 如果允许用户与提供商建立多个连接，则为true
         */
        public boolean isMultiProviderUserId() {
            return multiProviderUserId;
        }

        public boolean isAuthenticatePossible() {
            return !isMultiUserId();
        }
    }
}
