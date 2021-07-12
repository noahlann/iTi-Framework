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

import org.lan.iti.cloud.iha.server.IhaServer;
import org.lan.iti.cloud.iha.server.config.JwtConfig;
import org.lan.iti.common.extension.IExtension;
import org.lan.iti.common.extension.annotation.Extension;

/**
 * User/organization/enterprise and other identity service related interfaces
 *
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@Extension
public interface IdentityService extends IExtension<Object> {
    @Override
    default boolean matches(Object params) {
        return true;
    }

    /**
     * Get the jwt token encryption key string, The default is the scoped global jwt config configured in ids config.
     *
     * @param identity User/organization/enterprise identification
     * @return Encryption key string in json format
     */
    default String getJwksJson(String identity) {
        return IhaServer.getIhaServerConfig().getJwtConfig().getJwksJson();
    }

    /**
     * Get the configuration of jwt token encryption, The default is the scoped global jwt config configured in ids config.
     * <p>
     * 调用者需要根据
     *
     * @param clientId The client id of the client that currently needs to be authorized
     * @return Encryption key string in json format
     */
    default JwtConfig getJwtConfig(String clientId) {
        return IhaServer.getIhaServerConfig().getJwtConfig();
    }
}
