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

package org.lan.iti.common.security.authority.service;

import org.lan.iti.common.scanner.ResourceCache;
import org.lan.iti.common.scanner.model.ResourceDefinition;

import java.util.List;

/**
 * 默认资源服务，获取本服务资源
 *
 * @author NorthLan
 * @date 2020-05-09
 * @url https://noahlan.com
 */
public class DefaultAuthorityService implements AuthorityService {
    @Override
    public List<ResourceDefinition> getAllResources(String serviceCode) {
        return ResourceCache.getAllResources();
    }
}
