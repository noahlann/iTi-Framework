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

package org.lan.iti.cloud.iha.core.exception;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
public class IhaSocialException extends IhaException {
    private static final long serialVersionUID = 7128032020550995104L;

    public IhaSocialException(String message) {
        super(message);
    }

    public IhaSocialException(String message, Throwable cause) {
        super(message, cause);
    }

    public IhaSocialException(Throwable cause) {
        super(cause);
    }

    public IhaSocialException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
