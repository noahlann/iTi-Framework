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

import lombok.experimental.UtilityClass;
import org.lan.iti.cloud.iha.core.context.IhaAuthentication;
import org.lan.iti.cloud.iha.oauth2.OAuth2Util;
import org.lan.iti.cloud.iha.oauth2.OAuthConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@UtilityClass
public class PkceHelper {
    /**
     * Create the parameters required by PKCE
     *
     * @param oAuthConfig oauth config
     * @return Map
     * @see <a href="https://tools.ietf.org/html/rfc7636#section-1.1" target="_blank">1.1. Protocol Flow</a>
     * @see <a href="https://tools.ietf.org/html/rfc7636#section-4.1" target="_blank">4.1. Client Creates a Code Verifier</a>
     * @see <a href="https://tools.ietf.org/html/rfc7636#section-4.2" target="_blank">4.2. Client Creates the Code Challenge</a>
     * @see <a href="https://tools.ietf.org/html/rfc7636#section-4.3" target="_blank"> Client Sends the Code Challenge with the Authorization Request</a>
     */
    public static Map<String, Object> generatePkceParameters(OAuthConfig oAuthConfig) {
        /*
        After the pkce enhancement protocol is enabled, the generation method of challenge code derived from
        the code verifier sent in the authorization request is `s256` by default
         */
        PkceCodeChallengeMethod pkceCodeChallengeMethod = Optional.ofNullable(oAuthConfig.getCodeChallengeMethod())
                .orElse(PkceCodeChallengeMethod.S256);

        Map<String, Object> params = new HashMap<>(2);
        String codeVerifier = OAuth2Util.generateCodeVerifier();
        String codeChallenge = OAuth2Util.generateCodeChallenge(pkceCodeChallengeMethod, codeVerifier);
        params.put(PkceParams.CODE_CHALLENGE, codeChallenge);
        params.put(PkceParams.CODE_CHALLENGE_METHOD, pkceCodeChallengeMethod);
        // The default cache is local map.
        IhaAuthentication.getContext().getCache().set(oAuthConfig.getClientId(), codeVerifier, oAuthConfig.getCodeVerifierTimeout());
        return params;
    }

    /**
     * Gets the {@code code_verifier} in the cache
     *
     * @param clientId oauth clientId
     * @return {@code code_verifier}
     */
    public static String getCacheCodeVerifier(String clientId) {
        return (String) IhaAuthentication.getContext().getCache().get(clientId);
    }
}
