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

package org.lan.iti.common.gateway.support;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.gateway.model.RouteDefinitionVo;

import java.util.*;


/**
 * 路由缓存工具类
 *
 * @author NorthLan
 * @date 2020-03-09
 * @url https://noahlan.com
 */
@UtilityClass
@Slf4j
public class RouteCacheHolder {
    //    private List<RouteDefinitionVo> CACHE = new ArrayList<>();
    private final Map<String, RouteDefinitionVo> routes = Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * 获取缓存长度
     */
    public int size() {
        return routes.size();
    }

    /**
     * 获取路由列表
     */
    public Collection<RouteDefinitionVo> getRouteList() {
        return routes.values();
    }

    /**
     * 批量添加路由信息
     *
     * @param routeDefinitionVos 新的路由列表
     */
    public void add(List<RouteDefinitionVo> routeDefinitionVos) {
        routeDefinitionVos.forEach(it -> {
            routes.put(it.getId(), it);
        });
    }

    /**
     * 添加路由信息
     *
     * @param routeDefinitionVo 添加路由信息
     */
    public void add(RouteDefinitionVo routeDefinitionVo) {
        routes.put(routeDefinitionVo.getId(), routeDefinitionVo);
    }

    /**
     * 清理
     */
    public void clear() {
        routes.clear();
    }
}
