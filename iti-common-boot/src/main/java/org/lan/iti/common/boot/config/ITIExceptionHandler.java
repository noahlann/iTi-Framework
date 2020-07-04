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
import org.lan.iti.common.core.properties.ErrorCodeProperties;
import org.lan.iti.common.model.response.ApiResult;
import org.lan.iti.common.model.response.ArgumentInvalidResult;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * 框架异常拦截统一处理
 *
 * @author NorthLan
 * @date 2020-02-22
 * @url https://noahlan.com
 */
@Slf4j
@RestControllerAdvice
@Order(OrderConstants.ITI_EXCEPTION_HANDLER_ORDER)
@AllArgsConstructor
public class ITIExceptionHandler {
    private final ErrorCodeProperties properties;

    private int getMark() {
        return properties.getMark();
    }

    private boolean isErrorCodeEnabled() {
        return properties.isEnabled();
    }

    @ExceptionHandler(AbstractException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<?> handleServiceException(AbstractException e) {
        log.error("服务具体异常：", e);
        String errorCode = String.valueOf(e.getCode());
        if (isErrorCodeEnabled()) {
            errorCode = ErrorCode.builder()
                    .version(e.getVersion())
                    .mark(getMark())
                    .type(e.getType())
                    .level(e.getLevel())
                    .code(e.getCode())
                    .build()
                    .toString();
        }
        return ApiResult.error(errorCode, e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<?> handleBodyValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<ArgumentInvalidResult> results = new ArrayList<>();
        fieldErrors.forEach(it -> results.add(new ArgumentInvalidResult(
                it.getDefaultMessage(),
                it.getObjectName(),
                it.getField(),
                it.getRejectedValue())));
        log.error("参数验证错误, {}", results.toString());
        String errorCode = String.valueOf(ITIExceptionEnum.METHOD_ARGUMENT_NOT_VALID.getCode());
        if (isErrorCodeEnabled()) {
            errorCode = ErrorCode.builder()
                    .version(ITIConstants.EXCEPTION_ERROR_CODE_VERSION)
                    .mark(getMark())
                    .type(ErrorTypeEnum.BIZ.getValue())
                    .level(ErrorLevelEnum.PRIMARY.getValue())
                    .code(ITIExceptionEnum.METHOD_ARGUMENT_NOT_VALID.getCode())
                    .build().toString();
        }
        return ApiResult.error(errorCode,
                ITIExceptionEnum.METHOD_ARGUMENT_NOT_VALID.getMsg(),
                results);
    }

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<?> handleSQLException(SQLException e) {
        log.error("数据库异常：", e);
        String errorCode = String.valueOf(e.getErrorCode());
        if (isErrorCodeEnabled()) {
            errorCode = ErrorCode.builder()
                    .version(ITIConstants.EXCEPTION_ERROR_CODE_VERSION)
                    .mark(getMark())
                    .type(ErrorTypeEnum.EXT.getValue())
                    .level(ErrorLevelEnum.IMPORTANT.getValue())
                    .code(e.getErrorCode())
                    .build().toString();
        }
        return ApiResult.error(errorCode, e.getMessage());
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<?> handleBadSQLException(BadSqlGrammarException e) {
        log.error("SQL异常：", e);
        String errorCode = String.valueOf(ITIExceptionEnum.BAD_SQL.getCode());
        if (isErrorCodeEnabled()) {
            errorCode = ErrorCode.builder()
                    .version(ITIConstants.EXCEPTION_ERROR_CODE_VERSION)
                    .mark(getMark())
                    .type(ErrorTypeEnum.EXT.getValue())
                    .level(ErrorLevelEnum.IMPORTANT.getValue())
                    .code(ITIExceptionEnum.BAD_SQL.getCode())
                    .build().toString();
        }
        return ApiResult.error(errorCode, ITIExceptionEnum.BAD_SQL.getMsg());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<?> handleException(RuntimeException e) {
        log.error("运行时异常，未知错误：", e);
        String errorCode = String.valueOf(ITIExceptionEnum.INTERNAL_SERVER_ERROR.getCode());
        if (isErrorCodeEnabled()) {
            errorCode = ErrorCode.builder()
                    .version(ITIConstants.EXCEPTION_ERROR_CODE_VERSION)
                    .mark(getMark())
                    .type(ErrorTypeEnum.EXT.getValue())
                    .level(ErrorLevelEnum.IMPORTANT.getValue())
                    .code(ITIExceptionEnum.INTERNAL_SERVER_ERROR.getCode())
                    .build().toString();
        }
        return ApiResult.error(errorCode, e.getMessage());
    }
}
