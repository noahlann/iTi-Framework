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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


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
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<String> handleServiceException(ServiceException e) {
        log.error("服务异常：", e);
        return ApiResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<Object> handleBusinessException(BusinessException e) {
        log.error("业务异常：", e);
        return ApiResult.error(e.getCode(), e.getMessage(), e.getData());
    }
}
