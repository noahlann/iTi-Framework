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

package org.lan.iti.cloud.security.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import org.lan.iti.cloud.security.component.ITIAuth2ExceptionSerializer;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

/**
 * 自定义 OAuth2Exception
 *
 * @author NorthLan
 * @date 2020-02-24
 * @url https://noahlan.com
 */
@JsonSerialize(using = ITIAuth2ExceptionSerializer.class)
public class ITIAuth2Exception extends OAuth2Exception {
    private static final long serialVersionUID = 4524135638782085706L;

    @Getter
    private String errorCode;

    public ITIAuth2Exception(String msg, Throwable t) {
        super(msg, t);
    }

    public ITIAuth2Exception(String msg) {
        super(msg);
    }

    public ITIAuth2Exception(String msg, String errorCode) {
        super(msg);
        this.errorCode = errorCode;
    }
}
