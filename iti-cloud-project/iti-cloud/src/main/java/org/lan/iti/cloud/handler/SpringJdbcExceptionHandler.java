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
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * spring-jdbc 异常处理
 *
 * @author NorthLan
 * @date 2021-07-22
 * @url https://noahlan.com
 */
@Slf4j
@Order(AopConstants.SPRING_JDBC_EXCEPTION_HANDLER)
@RestControllerAdvice
public class SpringJdbcExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<String> handleBadSqlException(BadSqlGrammarException e) {
        log.error("SQL语法错误：", e);
        return ExceptionHandlerHelper.sqlException(e.getSQLException(), "数据库（语法）错误");
    }
}
