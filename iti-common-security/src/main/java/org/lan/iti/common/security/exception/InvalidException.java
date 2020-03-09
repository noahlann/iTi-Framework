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

package org.lan.iti.common.security.exception;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.lan.iti.common.security.component.ITIAuth2ExceptionSerializer;
import org.springframework.http.HttpStatus;

/**
 * 参数错误 异常
 *
 * @author NorthLan
 * @date 2020-02-25
 * @url https://noahlan.com
 */
@JsonSerialize(using = ITIAuth2ExceptionSerializer.class)
public class InvalidException extends ITIAuth2Exception {
    private static final long serialVersionUID = 6183181425292182632L;

    public InvalidException(String msg, Throwable t) {
        super(msg);
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "invalid_exception";
    }

    @Override
    public int getHttpErrorCode() {
        return HttpStatus.UPGRADE_REQUIRED.value();
    }
}
