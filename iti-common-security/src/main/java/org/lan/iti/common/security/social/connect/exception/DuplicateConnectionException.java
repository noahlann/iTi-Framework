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
import org.lan.iti.common.security.social.connect.ConnectionKey;

/**
 * 重复绑定异常（库中已存在对应的Connection，无需添加）
 *
 * @author NorthLan
 * @date 2020-03-19
 * @url https://noahlan.com
 */
public class DuplicateConnectionException extends ConnectionServiceException {

    private static final long serialVersionUID = 8278201215388701576L;

    @Getter
    private final ConnectionKey connectionKey;

    public DuplicateConnectionException(ConnectionKey connectionKey) {
        super("The connection with key " + connectionKey + " already exists");
        this.connectionKey = connectionKey;
    }
}
