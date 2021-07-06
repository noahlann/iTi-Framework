/*
 *
 *  * Copyright (c) [2019-2021] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.cloud.iha.server.oidc;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * According to standard specifications, construct id token
 *
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#IDToken" target="_blank">ID Token</a>
 * @see <a href="https://openid.net/specs/openid-connect-core-1_0.html#StandardClaims" target="_blank">Standard Claims</a>
 */
@Data
@Accessors(chain = true)
@SuperBuilder(toBuilder = true)
public class IdToken implements Serializable {
    private static final long serialVersionUID = 8539733079148504374L;

    /**
     * {@code iss} - the Issuer identifier
     */
    private String iss;

    /**
     * {@code sub} - the Subject identifier
     */
    private String sub;

    /**
     * {@code aud} - the Audience(s) that the ID Token is intended for
     */
    private String aud;

    /**
     * {@code exp} - the Expiration time on or after which the ID Token MUST NOT be accepted
     */
    private Long exp;

    /**
     * {@code iat} - the time at which the ID Token was issued
     */
    private Long iat;

    /**
     * {@code auth_time} - the time when the End-User authentication occurred
     */
    private String auth_time;

    /**
     * {@code nonce} - a {@code String} value used to associate a Client session with an ID Token,
     * and to mitigate replay attacks.
     */
    private String nonce;

    /**
     * {@code acr} - the Authentication Context Class Reference
     */
    private String acr;

    /**
     * {@code amr} - the Authentication Methods References
     */
    private String amr;

    /**
     * {@code azp} - the Authorized party to which the ID Token was issued
     */
    private String azp;

    /**
     * {@code at_hash} - the Access Token hash value
     */
    private String at_hash;

    /**
     * {@code c_hash} - the Authorization Code hash value
     */
    private String c_hash;

    private Object extra;
}
