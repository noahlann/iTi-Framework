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

package org.lan.iti.common.security.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.lan.iti.common.core.util.SystemClock;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;

/**
 * 自定义已授权权限标识
 *
 * @author NorthLan
 * @date 2020-03-03
 * @url https://noahlan.com
 */
@Data
@Builder
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseAuthority implements GrantedAuthority {
    private static final long serialVersionUID = -8484605685925055726L;

    /**
     * 权限ID
     */
    private String authorityId;

    /**
     * 权限标识
     */
    private String authority;

    /**
     * 过期时间
     */
    private Date expiredTime;

    /**
     * 权限拥有者
     */
    private String owner;

    /**
     * 权限 是否过期
     */
    public boolean isExpired() {
        return expiredTime != null && SystemClock.now() > expiredTime.getTime();
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }
}
