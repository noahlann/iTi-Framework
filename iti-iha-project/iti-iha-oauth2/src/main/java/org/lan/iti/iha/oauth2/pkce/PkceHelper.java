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

package org.lan.iti.iha.oauth2.pkce;

import lombok.experimental.UtilityClass;
import org.lan.iti.iha.oauth2.OAuth2Config;
import org.lan.iti.iha.oauth2.util.OAuth2Util;
import org.lan.iti.iha.security.IhaSecurity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
     * @param oAuth2Config oauth config
     * @return Map
     * @see <a href="https://tools.ietf.org/html/rfc7636#section-1.1" target="_blank">1.1. Protocol Flow</a>
     * @see <a href="https://tools.ietf.org/html/rfc7636#section-4.1" target="_blank">4.1. Client Creates a Code Verifier</a>
     * @see <a href="https://tools.ietf.org/html/rfc7636#section-4.2" target="_blank">4.2. Client Creates the Code Challenge</a>
     * @see <a href="https://tools.ietf.org/html/rfc7636#section-4.3" target="_blank"> Client Sends the Code Challenge with the Authorization Request</a>
     */
    public static Map<String, Object> generatePkceParameters(OAuth2Config oAuth2Config) {
        /*
        After the pkce enhancement protocol is enabled, the generation method of challenge code derived from
        the code verifier sent in the authorization request is `s256` by default
         */
        CodeChallengeMethod codeChallengeMethod = Optional.ofNullable(oAuth2Config.getCodeChallengeMethod())
                .orElse(CodeChallengeMethod.S256);

        Map<String, Object> params = new HashMap<>(2);
        String codeVerifier = OAuth2Util.generateCodeVerifier();
        String codeChallenge = OAuth2Util.generateCodeChallenge(codeChallengeMethod, codeVerifier);
        params.put(PkceParams.CODE_CHALLENGE, codeChallenge);
        params.put(PkceParams.CODE_CHALLENGE_METHOD, codeChallengeMethod);
        // The default cache is local map.
        IhaSecurity.getContext().getCache().put(oAuth2Config.getClientId(), codeVerifier, oAuth2Config.getCodeVerifierTimeout(), TimeUnit.MILLISECONDS);
        return params;
    }

    /**
     * Gets the {@code code_verifier} in the cache
     *
     * @param clientId oauth clientId
     * @return {@code code_verifier}
     */
    public static String getCacheCodeVerifier(String clientId) {
        return (String) IhaSecurity.getContext().getCache().get(clientId);
    }
}
