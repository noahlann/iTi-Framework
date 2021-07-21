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

package org.lan.iti.iha.core.exception;

import lombok.Getter;
import org.lan.iti.iha.core.result.IhaErrorCode;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@Getter
public class IhaException extends RuntimeException {
    private static final long serialVersionUID = -5421480473144769798L;

    protected int code;

    public IhaException(String message) {
        super(message);
        this.code = IhaErrorCode.ERROR.getCode();
    }

    public IhaException(IhaErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public IhaException(String message, Throwable cause) {
        super(message, cause);
    }

    public IhaException(Throwable cause) {
        super(cause);
    }

    public IhaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
