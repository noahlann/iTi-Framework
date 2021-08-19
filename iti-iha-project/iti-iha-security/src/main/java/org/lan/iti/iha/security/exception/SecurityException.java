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

package org.lan.iti.iha.security.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.lan.iti.common.core.support.IEnum;

/**
 * SecurityException
 *
 * @author NorthLan
 * @date 2021/7/28
 * @url https://blog.noahlan.com
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SecurityException extends RuntimeException {
    private static final long serialVersionUID = 9010068366546933858L;

    private String error;

    public SecurityException(String message) {
        super(message);
    }

    public SecurityException(IEnum<String> spec) {
        this(spec.getCode(), spec.getMessage());
    }

    public SecurityException(String error, String message) {
        super(message);
        this.error = error;
    }
}
