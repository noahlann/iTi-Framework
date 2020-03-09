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

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.model.response.ApiResult;
import org.lan.iti.common.core.constants.ITIConstants;
import org.lan.iti.common.core.enums.StatusEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 客户端异常处理
 * <p>
 * 401 未登录
 * 可以根据 AuthenticationException 不同细化异常处理
 *
 * @author NorthLan
 * @date 2020-02-24
 * @url https://noahlan.com
 */
@Slf4j
@Component
@AllArgsConstructor
public class ResourceAuthExceptionEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setCharacterEncoding(ITIConstants.UTF_8);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResult<String> result = ApiResult.empty();
        result.setStatus(StatusEnum.FAIL.getCode());

        if (authException != null) {
            result.setMsg(authException.getMessage())
                    .setData(authException.getMessage());
        }
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        @Cleanup PrintWriter writer = response.getWriter();
        writer.append(objectMapper.writeValueAsString(result));
    }
}
