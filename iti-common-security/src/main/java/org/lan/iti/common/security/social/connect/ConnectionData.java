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

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 一个数据传输对象，它可以保留Connection的内部状态并在应用程序的各层之间进行传输。
 * 根据{@link Connection}的特定类型，某些字段可能为空。
 *
 * @author NorthLan
 * @date 2020-03-17
 * @url https://noahlan.com
 */
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class ConnectionData implements Serializable {
    private static final long serialVersionUID = -1593566761608366694L;

    /**
     * 服务提供商ID
     */
    private String providerId;

    /**
     * 当前连接的服务提供商给定的用户ID
     */
    private String providerUserId;

    /**
     * 展示名称
     */
    private String displayName;

    /**
     * 获取用户资料url
     */
    private String profileUrl;

    /**
     * 用户图片url (通常为头像)
     */
    private String imageUrl;

    private String accessToken;

    private String secret;

    private String refreshToken;

    private Long expireTime;
}
