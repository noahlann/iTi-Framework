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

package org.lan.iti.iha.security.jwt;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Generate/verify the global configuration of jwt token.
 * If the caller needs to configure a set of jwt config for each client,
 * you can specify jwt config when obtaining the token.
 *
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@Data
@Accessors(chain = true)
public class JwtConfig {
    /**
     * rsa encryption key id
     */
    private String jwksKeyId = "iha-jwk-keyid";
    /**
     * <strong>Optional</strong>, jwt token verification type, the default is {@code JwtVerificationType.HTTPS_JWKS_ENDPOINT}
     * <p>
     * The usage is as follows:
     * <p>
     * 1. If the public key of the jwt issuer is at the https jwks endpoint, please set it to {@code JwtVerificationType.HTTPS_JWKS_ENDPOINT}
     * <p>
     * 2. When using jwks certificate string verification, please set it to {@code JwtVerificationType.JWKS}
     * <p>
     * 3. When using x.509 certificate string verification, please set it to {@code JwtVerificationType.X_509}
     */
    private JwtVerificationType jwtVerificationType = JwtVerificationType.HTTPS_JWKS_ENDPOINT;
    /**
     * <strong>Optional</strong>, when {@link JwtConfig#jwtVerificationType} is equal to {@code JWKS}, this attribute is <strong>required</strong>
     */
    private String jwksJson;
    /**
     * jwt token encryption algorithm, the default is {@code RS256}
     */
    private TokenAlgorithms tokenSigningAlg = TokenAlgorithms.RS256;
}
