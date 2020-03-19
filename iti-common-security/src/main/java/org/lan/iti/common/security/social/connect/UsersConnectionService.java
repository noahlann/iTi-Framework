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

import java.util.List;
import java.util.Set;

/**
 * 用户连接数据访问接口
 * 用于管理用户到服务提供商的连接的全局存储
 *
 * @author NorthLan
 * @date 2020-03-19
 * @url https://noahlan.com
 */
public interface UsersConnectionService {

    List<String> findUserIdsWithConnection(Connection<?> connection);

    Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds);

    ConnectionService createConnectionService(String userId);
}
