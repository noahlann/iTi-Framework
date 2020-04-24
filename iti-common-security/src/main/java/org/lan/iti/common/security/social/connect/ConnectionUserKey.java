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
import org.springframework.lang.NonNull;

import java.io.Serializable;
import java.util.Objects;

/**
 * 连接中的用户信息
 *
 * @author NorthLan
 * @date 2020-04-08
 * @url https://noahlan.com
 */
@Getter
@AllArgsConstructor
@ToString
public class ConnectionUserKey implements Serializable {
    private static final long serialVersionUID = -7974207145723036456L;

    private final String userId;

    @NonNull
    private final String domain;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionUserKey that = (ConnectionUserKey) o;
        return Objects.equals(userId, that.userId) &&
                domain.equals(that.domain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, domain);
    }
}
