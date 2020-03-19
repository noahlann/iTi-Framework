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

package org.lan.iti.common.security.social.exception;

import lombok.Getter;

/**
 * provider API 异常
 *
 * @author NorthLan
 * @date 2020-03-18
 * @url https://noahlan.com
 */
public class ApiException extends SocialException {
    private static final long serialVersionUID = -962819930196865362L;

    @Getter
    private String providerId;

    public ApiException(String providerId, String message) {
        super(message);
        this.providerId = providerId;
    }

    public ApiException(String providerId, String message, Throwable cause) {
        super(message, cause);
        this.providerId = providerId;
    }
}
