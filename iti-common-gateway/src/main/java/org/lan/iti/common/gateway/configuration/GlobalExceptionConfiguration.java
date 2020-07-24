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

package org.lan.iti.common.gateway.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.constants.ITIConstants;
import org.lan.iti.common.core.enums.ErrorLevelEnum;
import org.lan.iti.common.core.enums.ErrorTypeEnum;
import org.lan.iti.common.core.error.ErrorCode;
import org.lan.iti.common.core.properties.ErrorCodeProperties;
import org.lan.iti.common.model.response.ApiResult;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 网关全局异常处理器，仅作用于webflux下，优先级低于{@link org.springframework.web.server.handler.ResponseStatusExceptionHandler}
 *
 * @author NorthLan
 * @date 2020-07-02
 * @url https://noahlan.com
 */
@Slf4j
@Order(-2)
@Configuration
@RequiredArgsConstructor
public class GlobalExceptionConfiguration implements ErrorWebExceptionHandler {
    private final ErrorCodeProperties properties;
    private final ObjectMapper objectMapper;

    @NonNull
    @Override
    public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }
        // header set
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        if (ex instanceof ResponseStatusException) {
            response.setStatusCode(((ResponseStatusException) ex).getStatus());
        }

        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            Integer rawStatusCode = response.getRawStatusCode();
            if (rawStatusCode == null) {
                rawStatusCode = 500;
            }
            String errorCode = rawStatusCode.toString();
            if (properties.isEnabled()) {
                errorCode = ErrorCode.builder()
                        .version(ITIConstants.EXCEPTION_ERROR_CODE_VERSION)
                        .mark(properties.getMark())
                        .type(ErrorTypeEnum.BIZ.getValue())
                        .level(ErrorLevelEnum.FATAL.getValue())
                        .code(rawStatusCode)
                        .build()
                        .toString();
            }
            String msg = ex.getMessage();
            if (ex instanceof ResponseStatusException) {
                msg = ((ResponseStatusException) ex).getReason();
            }
            try {
                return bufferFactory.wrap(objectMapper.writeValueAsBytes(ApiResult.error(errorCode, msg)));
            } catch (JsonProcessingException e) {
                log.warn("Error writing response", ex);
                return bufferFactory.wrap(new byte[0]);
            }
        }));
    }
}
