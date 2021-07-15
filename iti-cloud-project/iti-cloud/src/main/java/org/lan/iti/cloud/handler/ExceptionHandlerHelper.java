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

import lombok.experimental.UtilityClass;
import org.lan.iti.common.core.api.ApiResult;
import org.lan.iti.common.core.enums.ITIExceptionEnum;
import org.lan.iti.common.core.exception.AbstractException;
import org.lan.iti.common.core.exception.BusinessException;
import org.lan.iti.common.core.exception.ServiceException;
import org.lan.iti.common.core.validator.ArgumentInvalidResult;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;

import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author NorthLan
 * @date 2021-04-29
 * @url https://noahlan.com
 */
@UtilityClass
public class ExceptionHandlerHelper {

    public ResponseEntity<ApiResult<String>> handle(Throwable e, Logger log) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        Integer code = ApiResult.DefaultEnum.FAIL.getCode();
        String message = e.getMessage();
        if (e instanceof AbstractException) {
            code = ((AbstractException) e).getCode();
            if (e instanceof ServiceException) {
                log.error("服务异常：", e);
            }
            if (e instanceof BusinessException) {
                log.error("业务异常：", e);
            }
        } else if (e instanceof MissingServletRequestParameterException) {
            code = ITIExceptionEnum.INTERNAL_SERVER_ERROR.getCode();
            status = HttpStatus.BAD_REQUEST;
            log.error("缺少请求参数：", e);
        } else if (e instanceof SQLException) {
            code = ((SQLException) e).getErrorCode();
            log.error("数据库异常：", e);
            // TODO jdbc依赖问题
//        } else if (e instanceof BadSqlGrammarException) {
//            code = ITIExceptionEnum.BAD_SQL.getCode();
//            message = ITIExceptionEnum.BAD_SQL.getMessage();
//            log.error("SQL异常：", e);
        } else if (e instanceof RuntimeException) {
            log.error("运行时异常，未知错误：", e);
        } else if (e instanceof Exception) {
            log.error("非预期异常：", e);
        }
        return ResponseEntity.status(status).body(ApiResult.error(code, message));
    }

    public ResponseEntity<ApiResult<String>> handleArgumentResult(BindingResult bindingResult, Logger log) {
        return handleArgumentResult(getBindingResult(bindingResult), log);
    }

    public ResponseEntity<ApiResult<String>> handleArgumentResult(ConstraintViolationException e, Logger log) {
        return handleArgumentResult(getViolationResults(e), log);
    }

    private ResponseEntity<ApiResult<String>> handleArgumentResult(List<ArgumentInvalidResult> results, Logger log) {
        log.error("参数验证错误, {}", results.toString());
        Integer errorCode = ITIExceptionEnum.METHOD_ARGUMENT_NOT_VALID.getCode();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResult.error(errorCode,
                ITIExceptionEnum.METHOD_ARGUMENT_NOT_VALID.getMessage(),
                results.stream().map(ArgumentInvalidResult::getDefaultMessage).collect(Collectors.joining("|"))));
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
