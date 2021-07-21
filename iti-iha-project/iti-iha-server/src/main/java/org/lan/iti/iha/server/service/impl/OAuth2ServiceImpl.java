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

package org.lan.iti.iha.server.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.iha.oauth2.GrantType;
import org.lan.iti.iha.server.IhaServer;
import org.lan.iti.iha.server.IhaServerConstants;
import org.lan.iti.iha.server.exception.InvalidCodeException;
import org.lan.iti.iha.server.exception.UnsupportedGrantTypeException;
import org.lan.iti.iha.server.model.AuthorizationCode;
import org.lan.iti.iha.server.model.IhaServerRequestParam;
import org.lan.iti.iha.server.model.UserDetails;
import org.lan.iti.iha.server.model.enums.ErrorResponse;
import org.lan.iti.iha.server.service.OAuth2Service;
import org.lan.iti.iha.server.util.OAuthUtil;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@Slf4j
public class OAuth2ServiceImpl implements OAuth2Service {

    @Override
    public String createAuthorizationCode(IhaServerRequestParam param, UserDetails userDetails, Long codeExpiresIn) {
        String code = RandomUtil.randomString(12);
        AuthorizationCode authorizationCode = AuthorizationCode.builder()
                .userDetails(userDetails)
                .scope(param.getScope())
                .nonce(param.getNonce())
                .codeChallenge(param.getCodeChallenge())
                .codeChallengeMethod(param.getCodeChallengeMethod())
                .build();
        IhaServer.getContext().getCache().set(IhaServerConstants.OAUTH_CODE_CACHE_KEY + code, authorizationCode, codeExpiresIn * 1000);
        return code;
    }

    @Override
    public AuthorizationCode validateAndGetAuthorizationCode(String grantType, String code) {
        if (!GrantType.AUTHORIZATION_CODE.getType().equals(grantType)) {
            throw new UnsupportedGrantTypeException(ErrorResponse.UNSUPPORTED_GRANT_TYPE);
        }
        AuthorizationCode authCode = this.getCodeInfo(code);
        if (null == authCode || ObjectUtil.hasNull(authCode.getUserDetails(), authCode.getScope())) {
            throw new InvalidCodeException(ErrorResponse.INVALID_CODE);
        }
        return authCode;
    }

    @Override
    public void validateAuthorizationCodeChallenge(String codeVerifier, String code) {
        log.debug("The client opened the pkce enhanced protocol and began to verify the legitimacy of the code challenge...");
        AuthorizationCode authCode = this.getCodeInfo(code);
        if (ObjectUtil.isNull(authCode)) {
            throw new InvalidCodeException(ErrorResponse.INVALID_CODE);
        }
        if (ObjectUtil.hasNull(authCode.getCodeChallenge(), authCode.getCodeChallengeMethod())) {
            log.debug("The client opened the pkce enhanced protocol, and the legality verification of the code challenge failed...");
            throw new InvalidCodeException(ErrorResponse.INVALID_CODE_CHALLENGE);
        }
        String codeChallengeMethod = authCode.getCodeChallengeMethod();
        String cacheCodeChallenge = authCode.getCodeChallenge();
        String currentCodeChallenge = OAuthUtil.generateCodeChallenge(codeChallengeMethod, codeVerifier);
        if (!currentCodeChallenge.equals(cacheCodeChallenge)) {
            throw new InvalidCodeException(ErrorResponse.INVALID_CODE_CHALLENGE);
        }
    }

    @Override
    public AuthorizationCode getCodeInfo(String code) {
        return (AuthorizationCode) IhaServer.getContext().getCache().get(IhaServerConstants.OAUTH_CODE_CACHE_KEY + code);
    }

    @Override
    public void invalidateCode(String code) {
        IhaServer.getContext().getCache().removeKey(IhaServerConstants.OAUTH_CODE_CACHE_KEY + code);
    }
}
