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

package org.lan.iti.cloud.scanner.exception;

import org.lan.iti.common.core.util.Formatter;

/**
 * Scanner异常封装
 *
 * @author NorthLan
 * @date 2020-03-06
 * @url https://noahlan.com
 */
public class ScannerException extends RuntimeException {
    private static final long serialVersionUID = 6464070975032226886L;

    public ScannerException(String format, Object... args) {
        super(Formatter.format(format, args));
    }

    public ScannerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 过滤多余堆栈信息,这里不太需要
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
