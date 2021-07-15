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

package org.lan.iti.common.core.exception;

import lombok.Getter;

import javax.validation.constraints.NotNull;

/**
 * 前、中台统一的错误码机制
 * <p>
 * <p>错误码在domain是已异常形式抛出，异常有穿透能力，方便使用。这里不再拘泥于严格的规范限制</p>
 * <p>在application层，可由框架统一转换为 ApiResult</p>
 * TODO i18n
 *
 * @author NorthLan
 * @date 2021-03-02
 * @url https://noahlan.com
 */
public class BusinessException extends AbstractException {
    private static final long serialVersionUID = 3719499343672609356L;

    /**
     * 中台定义的统一错误码.
     */
    protected IExceptionSpec errorReason;

    /**
     * (业务前台)个性化消息.
     * <p>
     * <p>例如，业务前台要求它抛出的错误消息，中台不要再加工，要原封不动地输出</p>
     */
    @Getter
    protected String custom;

    public BusinessException(@NotNull IExceptionSpec errorReason) {
        super();
        this.errorReason = errorReason;
    }

    public BusinessException withCustom(@NotNull String custom) {
        this.custom = custom;
        return this;
    }

    public static BusinessException withReason(@NotNull IExceptionSpec errorReason) {
        return new BusinessException(errorReason);
    }

    public Integer code() {
        return errorReason.getCode();
    }

    public String message() {
        return errorReason.getMessage();
    }

    public boolean hasCustom() {
        return custom != null;
    }

    @Override
    public String getMessage() {
        if (hasCustom()) {
            return this.custom;
        }
        return message();
    }

    @Override
    public Integer getCode() {
        return this.code();
    }

    /**
     * 移除堆栈信息
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
