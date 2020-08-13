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

package org.lan.iti.common.security.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.model.response.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * 鉴权服务器客户端异常处理 {@link AuthenticationException}
 * 不同细化处理
 *
 * @author NorthLan
 * @date 2020-07-30
 * @url https://noahlan.com
 */
@Slf4j
@RequiredArgsConstructor
public class ResourceAuthExceptionEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ApiResult<String> result = ApiResult.error(HttpStatus.UNAUTHORIZED.value(), authException.getMessage());
        if (authException instanceof CredentialsExpiredException
                || authException instanceof InsufficientAuthenticationException) {
            String msg = SpringSecurityMessageSource.getAccessor().getMessage("AbstractUserDetailsAuthenticationProvider.credentialsExpired",
                    authException.getMessage(),
                    Locale.CHINA);
            result.setMsg(msg);
        }
        if (authException instanceof UsernameNotFoundException) {
            String msg = SpringSecurityMessageSource.getAccessor().getMessage(
                    "AbstractUserDetailsAuthenticationProvider.noopBindAccount", authException.getMessage(),
                    Locale.CHINA);
            result.setMsg(msg);
        }
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.sendError(HttpStatus.UNAUTHORIZED.value(), objectMapper.writeValueAsString(result));
    }
}
