///*
// *
// *  * Copyright (c) [2019-2021] [NorthLan](lan6995@gmail.com)
// *  *
// *  * Licensed under the Apache License, Version 2.0 (the "License");
// *  * you may not use this file except in compliance with the License.
// *  * You may obtain a copy of the License at
// *  *
// *  *     http://www.apache.org/licenses/LICENSE-2.0
// *  *
// *  * Unless required by applicable law or agreed to in writing, software
// *  * distributed under the License is distributed on an "AS IS" BASIS,
// *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  * See the License for the specific language governing permissions and
// *  * limitations under the License.
// *
// */
//
//package org.lan.iti.cloud.sentinel.handler;
//
//import com.alibaba.csp.sentinel.Tracer;
//import lombok.extern.slf4j.Slf4j;
//import org.lan.iti.common.core.api.ApiResult;
//import org.lan.iti.cloud.constants.AopConstants;
//import org.lan.iti.common.core.enums.ITIExceptionEnum;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//
///**
// * Sentinel环境下的全局异常拦截处理器
// *
// * @author NorthLan
// * @date 2021-03-13
// * @url https://noahlan.com
// */
//@Slf4j
//@Order(AopConstants.GLOBAL_EXCEPTION_HANDLER)
//@RestControllerAdvice
//public class SentinelGlobalExceptionHandler {
//
//    /**
//     * 全局异常
//     */
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ApiResult<String> handleException(Throwable e) {
//        log.error("全局异常：{}", e.getMessage(), e);
//
//        // 交给Sentinel处理
//        Tracer.trace(e);
//
//        return ApiResult.error(ITIExceptionEnum.INTERNAL_SERVER_ERROR.getCode(), e.getLocalizedMessage());
//    }
//}
