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

package org.lan.iti.common.security.component.pkce;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * code_challenge methods
 *
 * @author NorthLan
 * @date 2020-08-05
 * @url https://noahlan.com
 */
public enum CodeChallengeMethod {
    S256 {
        @Override
        public String encrypt(String codeVerifier) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(codeVerifier.getBytes(StandardCharsets.UTF_8));
                return Base64.getUrlEncoder().encodeToString(hash);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e);
            }
        }
    },
    PLAIN {
        @Override
        public String encrypt(String codeVerifier) {
            return codeVerifier;
        }
    },
    NONE {
        @Override
        public String encrypt(String codeVerifier) {
            throw new UnsupportedOperationException();
        }
    };

    public abstract String encrypt(String codeVerifier);
}
