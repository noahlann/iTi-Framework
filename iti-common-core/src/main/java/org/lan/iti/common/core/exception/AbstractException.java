/*
 *
 *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.common.core.exception;

import org.lan.iti.common.core.constants.ITIConstants;

/**
 * 框架异常抽象类
 * <pre>
 *     错误类型 {@link org.lan.iti.common.core.enums.ErrorTypeEnum} 为内建错误类型，业务项目可自定义但不可覆盖
 *     错误级别 {@link org.lan.iti.common.core.enums.ErrorLevelEnum} 为内建错误级别，业务项目可自定义但不可覆盖
 * </pre>
 *
 * @author NorthLan
 * @date 2020-05-14
 * @url https://noahlan.com
 */
public abstract class AbstractException extends RuntimeException {
    private static final long serialVersionUID = -1064327362741791267L;

    protected AbstractException() {
    }

    protected AbstractException(String message) {
        super(message);
    }

    protected AbstractException(String message, Throwable cause) {
        super(message, cause);
    }

    protected AbstractException(Throwable cause) {
        super(cause);
    }

    protected AbstractException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * 错误类型
     */
    public abstract int getType();

    /**
     * 错误级别
     */
    public abstract int getLevel();

    /**
     * 错误代码
     */
    public abstract int getCode();

    /**
     * 错误版本
     * 子类可覆盖后修改
     */
    public int getVersion() {
        return ITIConstants.EXCEPTION_ERROR_CODE_VERSION;
    }
}
