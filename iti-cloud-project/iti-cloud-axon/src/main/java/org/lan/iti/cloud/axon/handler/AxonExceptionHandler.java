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

package org.lan.iti.cloud.axon.handler;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.messaging.annotation.MessageHandlerInvocationException;
import org.lan.iti.cloud.constants.AopConstants;
import org.lan.iti.cloud.handler.ExceptionHandlerHelper;
import org.lan.iti.common.core.api.ApiResult;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author NorthLan
 * @date 2021-04-29
 * @url https://noahlan.com
 */
@Slf4j
@Order(AopConstants.AXON_EXCEPTION_HANDLER)
@RestControllerAdvice
public class AxonExceptionHandler {

    /**
     * Axon MessageHandlerInvocation
     * <p>消息发布异常</p>
     *
     * @param e 异常信息
     * @return 错误消息
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiResult<String>> handleServiceException(MessageHandlerInvocationException e) {
        log.error("消息发布异常：", e);
        return ExceptionHandlerHelper.handle(e.getCause() != null ? e.getCause() : e, log);
    }
}
