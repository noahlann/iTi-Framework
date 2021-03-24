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

package org.lan.iti.cloud.security.authority.service;

import org.lan.iti.cloud.scanner.model.ResourceDefinition;

import java.util.List;

/**
 * 资源服务
 *
 * @author NorthLan
 * @date 2020-05-09
 * @url https://noahlan.com
 */
public interface AuthorityService {
    /**
     * 获取某服务所有资源
     *
     * @param serviceCode 服务代码
     * @return 资源列表
     */
    List<ResourceDefinition> getAllResources(String serviceCode);
}
