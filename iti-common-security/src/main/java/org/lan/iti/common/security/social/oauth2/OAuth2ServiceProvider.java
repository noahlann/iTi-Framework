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

package org.lan.iti.common.security.social.oauth2;

import org.lan.iti.common.security.social.ServiceProvider;

/**
 * 基于oAuth 2.0 协议的服务提供接口
 *
 * @author NorthLan
 * @date 2020-03-30
 * @url https://noahlan.com
 */
public interface OAuth2ServiceProvider<A> extends ServiceProvider<A> {
    /**
     * 获取Api接口(针对于每个用户访问请求)
     *
     * @param accessToken api访问令牌
     * @return 绑定了服务提供商的 API
     */
    A getApi(String accessToken);

    // TODO getOAuth2Operations
}
