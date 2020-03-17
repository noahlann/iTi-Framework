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
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;

/**
 * 唯一业务密钥
 * 一个由providerId（例如"wechat"）加上providerUserId（例如"125660"）组成的组合键。
 * 为 {@link Connection}的equals()和hashCode()提供基础。
 *
 * @author NorthLan
 * @date 2020-03-17
 * @url https://noahlan.com
 */
@Getter
@AllArgsConstructor
@ToString
public class ConnectionKey implements Serializable {
    private static final long serialVersionUID = 3180322745500491962L;

    private final String providerId;

    private final String providerUserId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConnectionKey)) return false;
        ConnectionKey that = (ConnectionKey) o;
        return Objects.equals(providerId, that.providerId) &&
                Objects.equals(providerUserId, that.providerUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerId, providerUserId);
    }
}
