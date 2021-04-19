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

package org.lan.iti.common.core.exception;

import lombok.Getter;

/**
 * 服务异常统一规范
 *
 * @author NorthLan
 * @date 2020-05-14
 * @url https://noahlan.com
 */
public class ServiceException extends AbstractException {
    private static final long serialVersionUID = -6476522626904036566L;

    @Getter
    private final String code;

    public ServiceException(String code, String message) {
        super(message);
        this.code = code;
    }
}
