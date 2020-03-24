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

import lombok.Getter;

/**
 * 用户未绑定服务商
 *
 * @author NorthLan
 * @date 2020-03-19
 * @url https://noahlan.com
 */
public class NotConnectedException extends ConnectionRepositoryException {
    private static final long serialVersionUID = 5184825570124867701L;

    @Getter
    private String providerId;

    public NotConnectedException(String providerId) {
        super("Not connected to provider '" + providerId + "'");
        this.providerId = providerId;
    }
}
