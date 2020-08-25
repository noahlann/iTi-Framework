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
import org.lan.iti.common.core.properties.ErrorProperties;
import org.lan.iti.common.model.response.ApiResult;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * SQL异常全局拦截处理器
 *
 * @author NorthLan
 * @date 2020-07-30
 * @url https://noahlan.com
 */
@Slf4j
@RestControllerAdvice
@Order(OrderConstants.SQL_EXCEPTION_HANDLER)
@AllArgsConstructor
public class SqlExceptionHandler {
    private final ErrorProperties properties;

    @ExceptionHandler(SQLException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<?> handleSQLException(SQLException e) {
        log.error("数据库异常：", e);
        String errorCode = String.valueOf(e.getErrorCode());
        if (properties.isEnabled()) {
            errorCode = ErrorCode.builder()
                    .version(ITIConstants.EXCEPTION_ERROR_CODE_VERSION)
                    .mark(properties.getMark())
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
        if (properties.isEnabled()) {
            errorCode = ErrorCode.builder()
                    .version(ITIConstants.EXCEPTION_ERROR_CODE_VERSION)
                    .mark(properties.getMark())
                    .type(ErrorTypeEnum.EXT.getValue())
                    .level(ErrorLevelEnum.IMPORTANT.getValue())
                    .code(ITIExceptionEnum.BAD_SQL.getCode())
                    .build().toString();
        }
        return ApiResult.error(errorCode, ITIExceptionEnum.BAD_SQL.getMsg());
    }
}
