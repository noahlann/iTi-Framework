/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.common.security.component;

import org.lan.iti.common.security.exception.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultThrowableAnalyzer;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InsufficientScopeException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.web.HttpRequestMethodNotSupportedException;

/**
 * 自定义异常处理,重写oauth 默认实现
 *
 * @author NorthLan
 * @date 2020-03-03
 * @url https://noahlan.com
 */
public class ITIWebResponseExceptionTranslator implements WebResponseExceptionTranslator<OAuth2Exception> {
    private final ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();

    @Override
    public ResponseEntity<OAuth2Exception> translate(Exception e) {
        // Try to extract a SpringSecurityException from the stacktrace
        Throwable[] causeChain = throwableAnalyzer.determineCauseChain(e);
        Exception ase = (ServiceUnavailableException) throwableAnalyzer
                .getFirstThrowableOfType(ServiceUnavailableException.class, causeChain);

        // ServiceUnavailableException
        // 优先第一处理是因为在DaoAuthenticationProvider#retrieve中如果抛出自定义异常将会被包装，其堆栈信息将会排序后置
        // 如果使用Internal异常,将会翻译为401且其header会填入异常message(不支持中文)
        if (ase != null) {
            return handleOAuth2Exception(new ServiceUnavailableException(e.getMessage(), e));
        }

        ase = (AuthenticationException) throwableAnalyzer.getFirstThrowableOfType(AuthenticationException.class,
                causeChain);
        if (ase != null) {
            return handleOAuth2Exception(new UnauthorizedException(e.getMessage(), e));
        }

        ase = (AccessDeniedException) throwableAnalyzer
                .getFirstThrowableOfType(AccessDeniedException.class, causeChain);
        if (ase != null) {
            return handleOAuth2Exception(new ForbiddenException(ase.getMessage(), ase));
        }

        ase = (InvalidGrantException) throwableAnalyzer
                .getFirstThrowableOfType(InvalidGrantException.class, causeChain);
        if (ase != null) {
            return handleOAuth2Exception(new InvalidException(ase.getMessage(), ase));
        }

        ase = (HttpRequestMethodNotSupportedException) throwableAnalyzer
                .getFirstThrowableOfType(HttpRequestMethodNotSupportedException.class, causeChain);
        if (ase != null) {
            return handleOAuth2Exception(new MethodNotAllowedException(ase.getMessage(), ase));
        }

        ase = (OAuth2Exception) throwableAnalyzer.getFirstThrowableOfType(
                OAuth2Exception.class, causeChain);

        if (ase != null) {
            return handleOAuth2Exception((OAuth2Exception) ase);
        }
        return handleOAuth2Exception(new ServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e));
    }

    private ResponseEntity<OAuth2Exception> handleOAuth2Exception(OAuth2Exception e) {
        int status = e.getHttpErrorCode();
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CACHE_CONTROL, "no-store");
        headers.set(HttpHeaders.PRAGMA, "no-cache");
        if (status == HttpStatus.UNAUTHORIZED.value() || (e instanceof InsufficientScopeException)) {
            headers.set(HttpHeaders.WWW_AUTHENTICATE, String.format("%s %s", OAuth2AccessToken.BEARER_TYPE, e.getSummary()));
        }
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 客户端异常直接返回客户端,不然无法解析
        if (e instanceof ClientAuthenticationException) {
            return new ResponseEntity<>(e, headers,
                    HttpStatus.valueOf(status));
        }
        return new ResponseEntity<>(new ITIAuth2Exception(e.getMessage(), e.getOAuth2ErrorCode()), headers,
                HttpStatus.valueOf(status));

    }
}
