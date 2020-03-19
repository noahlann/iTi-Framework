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

package org.lan.iti.common.security.social.connect;

/**
 * 用来设定值上的配置接口Connection从一个特定的服务提供商的API实例。
 * {@link #setProviderUserId(String)} 映射到 {@link ConnectionKey#getProviderUserId()}
 * {@link #setDisplayName(String)} 映射到 {@link Connection#getDisplayName()}
 * {@link #setProfileUrl(String)} 映射到 {@link Connection#getProfileUrl()}
 * {@link #setImageUrl(String)} 映射到 {@link Connection#getImageUrl()}
 *
 * @author NorthLan
 * @date 2020-03-18
 * @url https://noahlan.com
 */
public interface ConnectionValues {
    /**
     * 设置映射到 {@link ConnectionKey#getProviderUserId()} 的值
     *
     * @param providerUserId 提供商的用户身份
     */
    public void setProviderUserId(String providerUserId);

    /**
     * 设置映射到 {@link Connection#getDisplayName()} 的值
     *
     * @param displayName 用户的显示名称
     */
    public void setDisplayName(String displayName);

    /**
     * 设置映射到 {@link Connection#getProfileUrl()} 的值
     *
     * @param profileUrl 用户的个人资料网址
     */
    public void setProfileUrl(String profileUrl);

    /**
     * 设置映射到 {@link Connection#getImageUrl()} 的值
     *
     * @param imageUrl 用户的图片网址
     */
    public void setImageUrl(String imageUrl);
}
