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

package org.lan.iti.cloud.strategy.exception;

import org.lan.iti.common.core.util.Formatter;

/**
 * 策略异常
 *
 * @author NorthLan
 * @date 2021-05-15
 * @url https://noahlan.com
 */
public class StrategyException extends RuntimeException {
    private static final long serialVersionUID = -2584281330596389122L;

    public StrategyException() {
    }

    public StrategyException(String message) {
        super(message);
    }

    public StrategyException(String message, Throwable cause) {
        super(message, cause);
    }

    public StrategyException(Throwable cause) {
        super(cause);
    }

    public StrategyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrack) {
        super(message, cause, enableSuppression, writableStackTrack);
    }

    public static StrategyException ofMessage(String format, Object... args) {
        return new StrategyException(Formatter.format(format, args));
    }
}
