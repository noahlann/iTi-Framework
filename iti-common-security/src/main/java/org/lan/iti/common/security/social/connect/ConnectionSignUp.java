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

package org.lan.iti.common.security.social.connect;

/**
 * 在无法通过{@link Connection}映射用户ID的情况下，用于注册新用户的命令
 *
 * @author NorthLan
 * @date 2020-03-19
 * @url https://noahlan.com
 */
public interface ConnectionSignUp {

    /**
     * 注册新用户
     *
     * @param connection 连接信息
     * @return 新的用户ID。可以为null，表示无法创建隐式本地用户配置文件。
     */
    String execute(Connection<?> connection);
}
