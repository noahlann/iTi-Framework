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

package org.lan.iti.cloud.iha.core.result;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author NorthLan
 * @date 2021-07-05
 * @url https://noahlan.com
 */
@Data
@Accessors(chain = true)
public class IhaResponse implements Serializable {
    private static final long serialVersionUID = -8775916040068466418L;

    private int code;
    private String message;
    private Object data;

    public static IhaResponse success() {
        return new IhaResponse()
                .setCode(IhaErrorCode.SUCCESS.getCode())
                .setMessage(IhaErrorCode.SUCCESS.getMessage());
    }

    public static IhaResponse success(Object data) {
        return new IhaResponse()
                .setCode(200)
                .setData(data);
    }

    public static IhaResponse error(IhaErrorCode errorCode) {
        return new IhaResponse()
                .setCode(errorCode.getCode())
                .setMessage(errorCode.getMessage());
    }

    public static IhaResponse error(int errorCode, String errorMessage) {
        return new IhaResponse()
                .setCode(errorCode)
                .setMessage(errorMessage);
    }

    /**
     * Whether the result currently returned is successful
     *
     * @return boolean
     */
    public boolean isSuccess() {
        return this.getCode() == HttpStatus.HTTP_OK;
    }

    /**
     * Methods provided for social, oauth, and oidc login strategy. The business flow is as follows:
     * <p>
     * 1. When not logged in, call the <code>strategy.authenticate(x)</code> method,
     * and the returned <code>JapResponse.data</code> is the authorize url of the third-party platform
     * <p>
     * 2. When the third-party login completes the callback, call the <code>strategy.authenticate(x)</code> method,
     * and the returned <code>JapResponse.data</code> is jap user
     * <p>
     * After calling the <code>strategy.authenticate(x)</code> method,
     * the developer can use <code>japResponse.isRedirectUrl()</code> to determine whether the current processing result
     * is the authorize url that needs to be redirected, or the jap user after successful login
     * <p>
     *
     * @return boolean
     */
    public boolean isRedirectUrl() {
        Object data = this.getData();
        return isSuccess()
                && ObjectUtil.isNotNull(data)
                && data instanceof String
                && ((String) data).startsWith("http");
    }
}
