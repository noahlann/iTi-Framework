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

package org.lan.iti.cloud.iha.server.service;

import org.lan.iti.cloud.iha.server.exception.IhaServerException;
import org.lan.iti.cloud.iha.server.model.IhaServerRequestParam;
import org.lan.iti.cloud.iha.server.model.UserDetails;
import org.lan.iti.common.extension.IExtension;
import org.lan.iti.common.extension.annotation.Extension;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@Extension
public interface UserDetailService extends IExtension<Object> {
    @Override
    default boolean matches(Object params) {
        return true;
    }

    /**
     * Login with RequestParam
     *
     * @param param RequestParam
     * @return User
     */
    default UserDetails loginByRequest(IhaServerRequestParam param) {
        throw new IhaServerException("Not implemented `loginByRequest(HttpServletRequest)`");
    }

    /**
     * Login with account and password.
     * <p>
     * In the business system, if it is a multi-tenant business architecture, a user may exist in multiple systems,
     * <p>
     * and the client id can distinguish the system where the user is located
     *
     * @param username account number
     * @param password password
     * @param clientId The unique code of the currently logged-in client
     * @return User
     */
    default UserDetails loginByUsernameAndPassword(String username, String password, String clientId) {
        throw new IhaServerException("Not implemented `loginByUsernameAndPassword(String, String, String)`");
    }

    /**
     * Get user info by userid.
     *
     * @param userId userId of the business system
     * @return User
     */
    default UserDetails getById(String userId) {
        throw new IhaServerException("Not implemented `getById(String)`");
    }

    /**
     * Get user info by username.
     * <p>
     * In the business system, if it is a multi-tenant business architecture, a user may exist in multiple systems,
     * <p>
     * and the client id can distinguish the system where the user is located
     *
     * @param username username of the business system
     * @param clientId The unique code of the currently logged-in client
     * @return User
     */
    default UserDetails getByUsername(String username, String clientId) {
        throw new IhaServerException("Not implemented `getByName(String, String)`");
    }
}
