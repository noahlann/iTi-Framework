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

import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.Optional;

/**
 * Pkce code 信息体
 *
 * @author NorthLan
 * @date 2020-08-05
 * @url https://noahlan.com
 */
public class PkceProtectedAuthentication {
    private final String codeChallenge;
    private final CodeChallengeMethod codeChallengeMethod;
    private final OAuth2Authentication authentication;

    public PkceProtectedAuthentication(OAuth2Authentication authentication) {
        this.codeChallenge = null;
        this.codeChallengeMethod = CodeChallengeMethod.NONE;
        this.authentication = authentication;
    }

    public PkceProtectedAuthentication(String codeChallenge, String codeChallengeMethod, OAuth2Authentication authentication) {
        this.authentication = authentication;
        this.codeChallengeMethod = getCodeChallengeMethod(codeChallengeMethod);
        this.codeChallenge = codeChallenge;
    }

    public PkceProtectedAuthentication(String codeChallenge, CodeChallengeMethod codeChallengeMethod, OAuth2Authentication authentication) {
        this.codeChallenge = codeChallenge;
        this.codeChallengeMethod = codeChallengeMethod;
        this.authentication = authentication;
    }

    /**
     * 是否为PKCE流程
     *
     * @return 若仅使用单参数构造(OAuth2Authentication), 则一定为true
     */
    public boolean isPkceFlow() {
        return this.codeChallengeMethod != CodeChallengeMethod.NONE;
    }

    /**
     * 验证并获取OAuth2Authentication
     *
     * @param codeVerify 验证串(PKCE客户端生成的随机串)
     * @return 基于验证后的结果
     */
    public OAuth2Authentication getOAuth2Authentication(String codeVerify) {
        if (!isPkceFlow()) {
            return authentication;
        }
        if (codeChallengeMethod.encrypt(codeVerify).equals(codeChallenge)) {
            return authentication;
        }
        throw new InvalidGrantException("Invalid code verifier.");
    }

    /**
     * 获取请求中的PKCE随机串加密方法
     *
     * @param codeChallengeMethod String类型的codeChallengeMethod
     * @return CodeChallengeMethod 枚举
     */
    private CodeChallengeMethod getCodeChallengeMethod(String codeChallengeMethod) {
        try {
            return Optional.ofNullable(codeChallengeMethod)
                    .map(String::toUpperCase)
                    .map(CodeChallengeMethod::valueOf)
                    .orElse(CodeChallengeMethod.PLAIN);
        } catch (IllegalArgumentException e) {
            throw new InvalidRequestException("Transform algorithm not supported");
        }
    }
}
