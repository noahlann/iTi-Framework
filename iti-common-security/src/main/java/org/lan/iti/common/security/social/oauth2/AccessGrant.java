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

import lombok.Getter;
import org.lan.iti.common.core.util.SystemClock;

import java.io.Serializable;

/**
 * OAuth2 Access token
 *
 * @author NorthLan
 * @date 2020-03-24
 * @url https://noahlan.com
 */
@Getter
public class AccessGrant implements Serializable {
    private static final long serialVersionUID = 7060964980646131654L;

    private final String accessToken;
    private final String scope;
    private final String refreshToken;
    private final Long expireTime;

    public AccessGrant(String accessToken) {
        this(accessToken, null, null, null);
    }

    public AccessGrant(String accessToken, String scope, String refreshToken, Long expireIn) {
        this.accessToken = accessToken;
        this.scope = scope;
        this.refreshToken = refreshToken;
        this.expireTime = expireIn != null ? SystemClock.now() + expireIn * 1000L : null;
    }
}
