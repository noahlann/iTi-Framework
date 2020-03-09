/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.common.gateway.annotation.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lan.iti.common.gateway.configuration.NacosDynamicRouteAutoConfiguration;
import org.lan.iti.common.gateway.configuration.RedisDynamicRouteAutoConfiguration;

/**
 * 动态路由类型
 *
 * @author NorthLan
 * @date 2020-03-09
 * @url https://noahlan.com
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public enum DynamicType {
    /**
     * 基于NacosConfig Server的动态路由
     */
    NACOS_CONFIG(NacosDynamicRouteAutoConfiguration.class),

    /**
     * 基于Redis共享的动态路由
     */
    REDIS(RedisDynamicRouteAutoConfiguration.class);

    private Class<?> configClass;
}
