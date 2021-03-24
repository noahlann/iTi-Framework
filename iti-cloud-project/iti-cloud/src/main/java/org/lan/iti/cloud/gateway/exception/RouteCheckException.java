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

package org.lan.iti.cloud.gateway.exception;

/**
 * 路由检查异常
 *
 * @author NorthLan
 * @date 2021-03-10
 * @url https://noahlan.com
 */
public class RouteCheckException extends RuntimeException {
    public RouteCheckException() {
    }

    public RouteCheckException(String message) {
        super(message);
    }

    public RouteCheckException(String message, Throwable cause) {
        super(message, cause);
    }

    public RouteCheckException(Throwable cause) {
        super(cause);
    }

    public RouteCheckException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
