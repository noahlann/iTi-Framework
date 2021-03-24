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

package org.lan.iti.common.core.exception;

/**
 * 框架启动过程 注册阶段产生的异常
 *
 * @author NorthLan
 * @date 2021-02-22
 * @url https://noahlan.com
 */
public class BootstrapException extends RuntimeException {
    private static final long serialVersionUID = 1149599546656626364L;

    private BootstrapException(String message) {
        super(message);
    }

    public static BootstrapException ofMessage(String... messages) {
        StringBuilder sb = new StringBuilder(100);
        for (String s : messages) {
            sb.append(s);
        }
        return new BootstrapException(sb.toString());
    }
}
