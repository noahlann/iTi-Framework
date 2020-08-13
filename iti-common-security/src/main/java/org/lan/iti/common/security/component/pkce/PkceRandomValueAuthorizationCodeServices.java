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

import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

/**
 * 支持Pkce的随机code生成
 *
 * @author NorthLan
 * @date 2020-08-05
 * @url https://noahlan.com
 */
public abstract class PkceRandomValueAuthorizationCodeServices implements PkceAuthorizationCodeServices {
    private final RandomValueStringGenerator generator = new RandomValueStringGenerator(32);

    protected abstract void store(String code, OAuth2Authentication authentication);

    protected abstract OAuth2Authentication remove(String code, @Nullable String codeVerifier);

    @Override
    public String createAuthorizationCode(OAuth2Authentication authentication) {
        String code = generator.generate();
        store(code, authentication);
        return code;
    }

    @Override
    public OAuth2Authentication consumeAuthorizationCode(String code) throws InvalidGrantException {
        OAuth2Authentication auth = this.remove(code, null);
        if (auth == null) {
            throw new InvalidGrantException("Invalid authorization code: " + code);
        }
        return auth;
    }

    @Override
    public OAuth2Authentication consumeAuthorizationCodeAndCodeVerifier(String code, String codeVerifier) throws InvalidGrantException {
        OAuth2Authentication auth = this.remove(code, codeVerifier);
        if (auth == null) {
            throw new InvalidGrantException("Invalid authorization code: " + code);
        }
        return auth;
    }
}
