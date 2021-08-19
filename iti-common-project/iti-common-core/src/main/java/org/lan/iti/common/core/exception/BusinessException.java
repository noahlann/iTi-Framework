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
import org.lan.iti.common.core.util.StringUtil;

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
     * (业务前台)个性化消息.
     * <p>
     * <p>例如，业务前台要求它抛出的错误消息，中台不要再加工，要原封不动地输出</p>
     */
    @Getter
    protected String custom;

    @Getter
    protected Object data;

    public BusinessException(String code, String message) {
        super(code, message);
    }

    public BusinessException(IExceptionSpec spec) {
        super(spec);
    }

    public BusinessException withCustom(@NotNull String custom) {
        this.custom = custom;
        return this;
    }

    public BusinessException withData(Object data) {
        this.data = data;
        return this;
    }

    public static BusinessException withReason(@NotNull IExceptionSpec errorReason) {
        return new BusinessException(errorReason);
    }

    @Override
    public String getMessage() {
        if (StringUtil.isNotEmpty(this.custom)) {
            return this.custom;
        }
        return super.getMessage();
    }

    /**
     * 移除堆栈信息
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
