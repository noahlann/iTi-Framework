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

package org.lan.iti.iha.security.csrf;


import cn.hutool.core.lang.Assert;
import lombok.Getter;

/**
 * @author NorthLan
 * @date 2021/8/13
 * @url https://blog.noahlan.com
 */
@Getter
public class DefaultCsrfToken implements CsrfToken {
    private final String token;
    private final String parameterName;
    private final String headerName;

    /**
     * Creates a new instance
     *
     * @param headerName    the HTTP header name to use
     * @param parameterName the HTTP parameter name to use
     * @param token         the value of the token (i.e. expected value of the HTTP parameter of
     *                      parametername).
     */
    public DefaultCsrfToken(String headerName, String parameterName, String token) {
        Assert.notEmpty(headerName, "headerName cannot be null or empty");
        Assert.notEmpty(parameterName, "parameterName cannot be null or empty");
        Assert.notEmpty(token, "token cannot be null or empty");
        this.headerName = headerName;
        this.parameterName = parameterName;
        this.token = token;
    }
}
