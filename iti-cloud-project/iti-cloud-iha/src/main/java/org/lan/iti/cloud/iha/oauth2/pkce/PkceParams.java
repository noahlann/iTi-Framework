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

package org.lan.iti.cloud.iha.oauth2.pkce;

/**
 * OAuth PKCE Parameters Registry
 *
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 * @see <a href="https://tools.ietf.org/html/rfc7636#section-6.1" target="_blank">6.1.  OAuth Parameters Registry</a>
 */
public interface PkceParams {
    /**
     * {@code code_challenge} - used in Authorization Request.
     */
    String CODE_CHALLENGE = "code_challenge";

    /**
     * {@code code_challenge_method} - used in Authorization Request.
     */
    String CODE_CHALLENGE_METHOD = "code_challenge_method";

    /**
     * {@code code_verifier} - used in Token Request.
     */
    String CODE_VERIFIER = "code_verifier";
}
