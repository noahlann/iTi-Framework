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

package org.lan.iti.cloud.handler;

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.api.ApiResult;
import org.lan.iti.cloud.constants.AopConstants;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author NorthLan
 * @date 2021-03-13
 * @url https://noahlan.com
 */
@Slf4j
@Order(AopConstants.GLOBAL_EXCEPTION_HANDLER)
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 避免 404 重定向到 /error 导致NPE ,ignore-url 需要配置对应端点
     *
     * @return ApiResult
     */
    @DeleteMapping("/error")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResult<String> noHandlerFoundException() {
        return ApiResult.error(HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase());
    }

    /**
     * 运行时异常处理,一般用作未知异常的处理器
     *
     * @param e 异常信息
     * @return 错误消息
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<String> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常，未知错误：", e);
        return ApiResult.error(e.getMessage());
    }

    /**
     * 全局异常（所有其它异常）
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<String> handleException(Throwable e) {
        log.error("全局非预期异常：", e);
        return ApiResult.error(e.getMessage());
    }
}
