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

package org.lan.iti.cloud.iha.server.model.enums;

/**
 * The verification type when the user verifies the jwt token (access token, refresh token, id token)，
 * For specific usage, please refer to {@link org.lan.iti.cloud.iha.server.util.JwtUtil#validateJwtToken(String, String, String, String)}
 *
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
public enum JwtVerificationType {
    /**
     * Using an HTTPS JWKS endpoint
     */
    HTTPS_JWKS_ENDPOINT,

    /**
     * Using JWKs
     */
    JWKS
}
