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
 * oAuth客户端无权调用api异常
 * - 调用带有已撤消或过期的访问令牌的API操作时
 * - 调用需要授权而不提供授权凭证的操作时
 *
 * @author NorthLan
 * @date 2020-03-30
 * @url https://noahlan.com
 */
public class NotAuthorizedException extends ApiException {
    private static final long serialVersionUID = -3258000525979919695L;

    public NotAuthorizedException(String providerId, String message) {
        super(providerId, message);
    }
}
