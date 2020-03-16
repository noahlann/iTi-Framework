/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.common.security.extend;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 扩展令牌
 *
 * @author NorthLan
 * @date 2020-03-12
 * @url https://noahlan.com
 */
public class ITIAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = -369677516673521140L;

    /**
     * 主体
     */
    private Object principal;

    /**
     * 凭证（密码）
     */
    private Object credentials;

    /**
     * 令牌类型
     */
    @Getter
    @Setter
    private String type;

    /**
     * 额外参数
     */
    @Getter
    private Map<String, String> extraMap;

    /**
     * Creates a token with the supplied array of authorities.
     *
     * @param authorities the collection of <tt>GrantedAuthority</tt>s for the principal
     *                    represented by this authentication object.
     */
    public ITIAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    public ITIAuthenticationToken(Object principal, Object credentials, String type, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.type = type;
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    /**
     * 添加额外参数
     *
     * @param key   键
     * @param value 值
     */
    public void addExtra(@NonNull String key, @NonNull String value) {
        if (extraMap == null) {
            extraMap = new HashMap<>();
        }
        extraMap.put(key, value);
    }

    /**
     * 获取额外参数
     *
     * @param key 键
     */
    @Nullable
    public Object getExtra(String key) {
        if (extraMap == null) {
            return null;
        }
        return extraMap.get(key);
    }
}
