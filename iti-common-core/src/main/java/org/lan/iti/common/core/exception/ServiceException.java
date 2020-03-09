/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.common.core.exception;

import lombok.Getter;
import org.lan.iti.common.core.exception.enums.IExceptionEnum;

/**
 * 业务异常统一规范
 *
 * @author NorthLan
 * @date 2020-02-22
 * @url https://noahlan.com
 */
@Getter
public class ServiceException extends RuntimeException {
    private static final long serialVersionUID = -6476522626904036566L;

    private Integer code;
    private Object body;

    public ServiceException(String message, int code, Object body) {
        super(message);
        this.code = code;
        this.body = body;
    }

    public ServiceException(String message, Throwable cause, int code, Object body) {
        super(message, cause);
        this.code = code;
        this.body = body;
    }

    public ServiceException(IExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMessage());
        this.code = exceptionEnum.getCode();
    }

//    @Override
//    public synchronized Throwable fillInStackTrace() {
//        return this;
//    }
}
