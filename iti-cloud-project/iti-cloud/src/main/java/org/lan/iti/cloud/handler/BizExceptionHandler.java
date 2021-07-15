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
import org.lan.iti.cloud.constants.AopConstants;
import org.lan.iti.common.core.api.ApiResult;
import org.lan.iti.common.core.exception.BusinessException;
import org.lan.iti.common.core.exception.ServiceException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;


/**
 * 业务异常拦截处理器
 *
 * @author NorthLan
 * @date 2020-02-22
 * @url https://noahlan.com
 */
@Slf4j
@Order(AopConstants.BIZ_EXCEPTION_HANDLER)
@RestControllerAdvice
public class BizExceptionHandler {

    /**
     * 框架服务异常处理
     *
     * @param e 异常信息
     * @return 错误消息
     */
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiResult<String>> handleServiceException(ServiceException e) {
        return ExceptionHandlerHelper.handle(e, log);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResult<String>> handleBusinessException(BusinessException e) {
        return ExceptionHandlerHelper.handle(e, log);
    }

    /**
     * 参数验证异常处理
     * requestBody | post
     *
     * @param e 异常信息
     * @return 转换后可识别的验证列表
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<String>> handleBodyValidException(MethodArgumentNotValidException e) {
        return ExceptionHandlerHelper.handleArgumentResult(e.getBindingResult(), log);
    }

    /**
     * 参数验证异常处理
     * get请求
     *
     * @param e 异常信息
     * @return 转换后可识别的验证列表
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResult<String>> handleBindException(BindException e) {
        return ExceptionHandlerHelper.handleArgumentResult(e, log);
    }

    /**
     * 参数验证异常处理
     * RequestParam
     *
     * @param e 异常信息
     * @return 转换后可识别的验证列表
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResult<String>> handleViolationException(ConstraintViolationException e) {
        return ExceptionHandlerHelper.handleArgumentResult(e, log);
    }

    /**
     * 请求参数缺失异常转换处理
     *
     * @param e 异常
     * @return 转换后可识别的验证列表
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResult<String>> handleMissingParameterException(MissingServletRequestParameterException e) {
        return ExceptionHandlerHelper.handle(e, log);
    }

    /**
     * 运行时异常处理,一般用作未知异常的处理器
     *
     * @param e 异常信息
     * @return 错误消息
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResult<String>> handleRuntimeException(RuntimeException e) {
        return ExceptionHandlerHelper.handle(e, log);
    }

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
}
