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

/**
 * oAuth客户端在调用api期间被拒绝的异常
 * - token验签失败
 * - token过期
 * - token被撤销
 *
 * @author NorthLan
 * @date 2020-03-30
 * @url https://noahlan.com
 */
public class RejectedAuthorizationException extends NotAuthorizedException {
    private static final long serialVersionUID = -7653265647545528172L;

    public RejectedAuthorizationException(String providerId, String message) {
        super(providerId, message);
    }
}
