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

package org.lan.iti.iha.oidc;

/**
 * Standard parameter names defined in the OAuth Parameters Registry and used by the
 * authorization endpoint and token endpoint.
 *
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 * @see <a target="_blank" href="https://openid.net/specs/openid-connect-core-1_0.html#OAuthParametersRegistry">18.2 OAuth Parameters Registration</a>
 */
public interface OidcParameterNames {
    /**
     * {@code id_token} - used in the Access Token Response.
     */
    String ID_TOKEN = "id_token";

    /**
     * {@code nonce} - used in the Authentication Request.
     */
    String NONCE = "nonce";

    /**
     * {@code response_mode}
     * <p>
     * OPTIONAL. Informs the Authorization Server of the mechanism to be used for returning parameters from the Authorization Endpoint. This use of this parameter is NOT RECOMMENDED when the Response Mode that would be requested is the default mode specified for the Response Type.
     */
    String RESPONSE_MODE = "response_mode";

    /**
     * {@code display} - used in the Authentication Request.
     */
    String DISPLAY = "display";

    /**
     * {@code prompt} - used in the Authentication Request.
     */
    String PROMPT = "prompt";

    /**
     * {@code max_age} - used in the Authentication Request.
     */
    String MAX_AGE = "max_age";

    /**
     * {@code id_token_hint} - used in the Authentication Request.
     */
    String ID_TOKEN_HINT = "id_token_hint";

    /**
     * {@code claims} - used in the Authentication Request.
     */
    String CLAIMS = "claims";

    /**
     * {@code auth_time} - the time when the End-User authentication occurred
     */
    String AUTH_TIME = "auth_time";

    /**
     * {@code acr} - the Authentication Context Class Reference
     */
    String ACR = "acr";
}
