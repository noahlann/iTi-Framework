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

package org.lan.iti.cloud.iha.server.context;

import lombok.Data;
import lombok.experimental.Accessors;
import org.lan.iti.cloud.iha.core.cache.IhaCache;
import org.lan.iti.cloud.iha.core.cache.IhaLocalCache;
import org.lan.iti.cloud.iha.server.config.IhaServerConfig;
import org.lan.iti.cloud.iha.server.model.UserDetails;
import org.lan.iti.cloud.iha.server.pipeline.Pipeline;
import org.lan.iti.cloud.iha.server.service.ClientDetailsService;
import org.lan.iti.cloud.iha.server.service.IdentityService;
import org.lan.iti.cloud.iha.server.service.UserDetailService;
import org.lan.iti.cloud.iha.server.service.UserStoreService;
import org.lan.iti.cloud.iha.server.service.impl.UserStoreServiceImpl;

import java.io.Serializable;

/**
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@Data
@Accessors(chain = true)
public class IhaServerContext implements Serializable {
    private static final long serialVersionUID = 4677418613923152037L;

    private IhaCache cache = new IhaLocalCache();

    private ClientDetailsService clientDetailsService;
    private UserDetailService userDetailService;
    private IdentityService identityService;
    private UserStoreService userStoreService = new UserStoreServiceImpl();
    private IhaServerConfig config;
    private Pipeline<Object> filterPipeline;
    private Pipeline<UserDetails> signInPipeline;
    private Pipeline<UserDetails> logoutPipeline;


    public IhaCache getCache() {
        return cache == null ? new IhaLocalCache() : cache;
    }
}
