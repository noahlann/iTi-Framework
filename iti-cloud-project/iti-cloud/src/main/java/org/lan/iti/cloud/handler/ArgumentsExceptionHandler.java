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
import org.lan.iti.common.core.enums.ITIExceptionEnum;
import org.lan.iti.common.core.validator.ArgumentInvalidResult;
import org.slf4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * 参数验证异常处理
 *
 * @author NorthLan
 * @date 2021-07-22
 * @url https://noahlan.com
 */
@Slf4j
@Order(AopConstants.ARGUMENTS_EXCEPTION_HANDLER)
@RestControllerAdvice
public class ArgumentsExceptionHandler {

    /**
     * 参数验证异常处理
     * requestBody | post
     *
     * @param e 异常信息
     * @return 转换后可识别的验证列表
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<List<ArgumentInvalidResult>> handleBodyValidException(MethodArgumentNotValidException e) {
        return handleArgumentResult(e.getBindingResult(), log);
    }

    /**
     * 参数验证异常处理
     * get请求
     *
     * @param e 异常信息
     * @return 转换后可识别的验证列表
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<List<ArgumentInvalidResult>> handleBindException(BindException e) {
        return handleArgumentResult(e, log);
    }

    /**
     * 参数验证异常处理
     * RequestParam
     *
     * @param e 异常信息
     * @return 转换后可识别的验证列表
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<List<ArgumentInvalidResult>> handleViolationException(ConstraintViolationException e) {
        return handleArgumentResult(e, log);
    }

    /**
     * 请求参数缺失异常转换处理
     *
     * @param e 异常
     * @return 转换后可识别的验证列表
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<String> handleMissingParameterException(MissingServletRequestParameterException e) {
        log.error("缺少请求参数：", e);
        return ApiResult.error(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    public ApiResult<List<ArgumentInvalidResult>> handleArgumentResult(BindingResult bindingResult, Logger log) {
        return handleArgumentResult(getBindingResult(bindingResult), log);
    }

    public ApiResult<List<ArgumentInvalidResult>> handleArgumentResult(ConstraintViolationException e, Logger log) {
        return handleArgumentResult(getViolationResults(e), log);
    }

    private ApiResult<List<ArgumentInvalidResult>> handleArgumentResult(List<ArgumentInvalidResult> result, Logger log) {
        log.error("参数验证错误, {}", result.toString());
        return ApiResult.error(HttpStatus.BAD_REQUEST.value(),
                ITIExceptionEnum.METHOD_ARGUMENT_NOT_VALID.getMessage(), result);
    }

    /**
     * 转换为参数错误列表
     *
     * @param bindingResult 绑定结果对象
     * @return 具体参数错误列表
     */
    private List<ArgumentInvalidResult> getBindingResult(BindingResult bindingResult) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        List<ArgumentInvalidResult> results = new ArrayList<>();
        fieldErrors.forEach(it -> results.add(new ArgumentInvalidResult(
                it.getDefaultMessage(),
                it.getObjectName(),
                it.getField(),
                it.getRejectedValue())));
        return results;
    }

    /**
     * 转换异常内参数为错误信息
     *
     * @param e 参数验证异常
     * @return 具体参数错误列表
     */
    private List<ArgumentInvalidResult> getViolationResults(ConstraintViolationException e) {
        List<ArgumentInvalidResult> results = new ArrayList<>();
        e.getConstraintViolations().forEach(it -> results.add(new ArgumentInvalidResult(
                it.getMessage(),
                it.getRootBeanClass().getSimpleName(),
                it.getPropertyPath().toString(),
                it.getInvalidValue()
        )));
        return results;
    }
}
