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

package org.lan.iti.common.security.social.nop;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lan.iti.common.core.util.SystemClock;

import java.io.Serializable;

/**
 * Nop token
 *
 * @author NorthLan
 * @date 2020-04-02
 * @url https://noahlan.com
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class NopGrant implements Serializable {
    private static final long serialVersionUID = -8214582928651586894L;

    private String providerUserId;
    //
    private String secret;
    //
    private String unionId;
    private String accessToken;
    private String refreshToken;
    private Long expireTime;
    private String scope;

    public NopGrant(String providerUserId, String secret, String unionId, Long expireIn) {
        this(providerUserId, secret, unionId,
                null, null,
                expireIn != null ? SystemClock.now() + expireIn * 1000L : null, null);
    }
}
