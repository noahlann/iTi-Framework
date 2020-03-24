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

package org.lan.iti.common.security.social.connect.exception;

import org.lan.iti.common.security.social.exception.SocialException;

/**
 * 基础Connection接口服务异常
 *
 * @author NorthLan
 * @date 2020-03-19
 * @url https://noahlan.com
 */
public abstract class ConnectionRepositoryException extends SocialException {
    private static final long serialVersionUID = 1189864946982617239L;

    public ConnectionRepositoryException(String message) {
        super(message);
    }

    public ConnectionRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
