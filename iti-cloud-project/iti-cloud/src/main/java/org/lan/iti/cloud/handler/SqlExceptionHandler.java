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
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.PersistenceException;
import java.sql.SQLException;

/**
 * SQL异常全局拦截处理器
 *
 * @author NorthLan
 * @date 2020-07-30
 * @url https://noahlan.com
 */
@Slf4j
@Order(AopConstants.SQL_EXCEPTION_HANDLER)
@RestControllerAdvice
public class SqlExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<String> handleSqlException(SQLException e) {
        log.error("数据库异常：", e);
        return ExceptionHandlerHelper.sqlException(e, "数据库（SQL）错误");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<String> handleBadSqlException(PersistenceException e) {
        log.error("数据库异常（持久化）：", e);
        return ApiResult.error(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                "数据库（持久化）错误", e.getLocalizedMessage());
    }
}
