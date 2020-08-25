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

package org.lan.iti.common.boot.config;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.constants.ITIConstants;
import org.lan.iti.common.core.constants.OrderConstants;
import org.lan.iti.common.core.enums.ErrorLevelEnum;
import org.lan.iti.common.core.enums.ErrorTypeEnum;
import org.lan.iti.common.core.enums.ITIExceptionEnum;
import org.lan.iti.common.core.error.ErrorCode;
import org.lan.iti.common.core.exception.AbstractException;
import org.lan.iti.common.core.properties.ErrorProperties;
import org.lan.iti.common.model.response.ApiResult;
import org.lan.iti.common.model.response.ArgumentInvalidResult;
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
import java.util.stream.Collectors;


/**
 * 框架异常拦截统一处理
 *
 * @author NorthLan
 * @date 2020-02-22
 * @url https://noahlan.com
 */
@Slf4j
@RestControllerAdvice
@Order(OrderConstants.GLOBAL_EXCEPTION_HANDLER)
@AllArgsConstructor
public class GlobalExceptionHandler {
    private final ErrorProperties properties;

    /**
     * 框架服务异常处理
     *
     * @param e 异常信息
     * @return 错误消息
     */
    @ExceptionHandler(AbstractException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<String> handleServiceException(AbstractException e) {
        log.error("服务具体异常：", e);
        String errorCode = String.valueOf(e.getCode());
        if (properties.isEnabled()) {
            errorCode = ErrorCode.builder()
                    .version(e.getVersion())
                    .mark(properties.getMark())
                    .type(e.getType())
                    .level(e.getLevel())
                    .code(e.getCode())
                    .build()
                    .toString();
        }
        return ApiResult.error(errorCode, e.getMessage());
    }

    /**
     * 参数验证异常处理
     * requestBody | post
     *
     * @param e 异常信息
     * @return 转换后可识别的验证列表
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<?> handleBodyValidException(MethodArgumentNotValidException e) {
        return handleArgumentResult(getBindingResult(e.getBindingResult()));
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
    public ApiResult<?> handleBindException(BindException e) {
        return handleArgumentResult(getBindingResult(e));
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
    public ApiResult<?> handleViolationException(ConstraintViolationException e) {
        return handleArgumentResult(getViolationResults(e));
    }

    private ApiResult<?> handleArgumentResult(List<ArgumentInvalidResult> results) {
        log.error("参数验证错误, {}", results.toString());
        String errorCode = String.valueOf(ITIExceptionEnum.METHOD_ARGUMENT_NOT_VALID.getCode());
        if (properties.isEnabled()) {
            errorCode = ErrorCode.builder()
                    .version(ITIConstants.EXCEPTION_ERROR_CODE_VERSION)
                    .mark(properties.getMark())
                    .type(ErrorTypeEnum.BIZ.getValue())
                    .level(ErrorLevelEnum.PRIMARY.getValue())
                    .code(ITIExceptionEnum.METHOD_ARGUMENT_NOT_VALID.getCode())
                    .build().toString();
        }
        if (properties.isArgumentsImplicitMode()) {
            return ApiResult.error(errorCode,
                    ITIExceptionEnum.METHOD_ARGUMENT_NOT_VALID.getMsg(),
                    results.stream().map(ArgumentInvalidResult::getDefaultMessage).collect(Collectors.joining("|")));
        } else {
            return ApiResult.error(errorCode,
                    ITIExceptionEnum.METHOD_ARGUMENT_NOT_VALID.getMsg(),
                    results);
        }
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
        e.getConstraintViolations().forEach(it -> {
            results.add(new ArgumentInvalidResult(
                    it.getMessage(),
                    it.getRootBeanClass().getSimpleName(),
                    it.getPropertyPath().toString(),
                    it.getInvalidValue()
            ));
        });
        return results;
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
        String errorCode = String.valueOf(ITIExceptionEnum.INTERNAL_SERVER_ERROR.getCode());
        if (properties.isEnabled()) {
            errorCode = ErrorCode.builder()
                    .version(ITIConstants.EXCEPTION_ERROR_CODE_VERSION)
                    .mark(properties.getMark())
                    .type(ErrorTypeEnum.EXT.getValue())
                    .level(ErrorLevelEnum.IMPORTANT.getValue())
                    .code(ITIExceptionEnum.INTERNAL_SERVER_ERROR.getCode())
                    .build().toString();
        }
        return ApiResult.error(errorCode, e.getMessage());
    }

    /**
     * 运行时异常处理,一般用作未知异常的处理器
     *
     * @param e 异常信息
     * @return 错误消息
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<String> handleException(RuntimeException e) {
        log.error("运行时异常，未知错误：", e);
        String errorCode = String.valueOf(ITIExceptionEnum.INTERNAL_SERVER_ERROR.getCode());
        if (properties.isEnabled()) {
            errorCode = ErrorCode.builder()
                    .version(ITIConstants.EXCEPTION_ERROR_CODE_VERSION)
                    .mark(properties.getMark())
                    .type(ErrorTypeEnum.EXT.getValue())
                    .level(ErrorLevelEnum.IMPORTANT.getValue())
                    .code(ITIExceptionEnum.INTERNAL_SERVER_ERROR.getCode())
                    .build().toString();
        }
        return ApiResult.error(errorCode, e.getMessage());
    }
}
