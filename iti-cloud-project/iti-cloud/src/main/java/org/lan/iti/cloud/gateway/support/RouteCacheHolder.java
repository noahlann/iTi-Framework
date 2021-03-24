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

package org.lan.iti.cloud.gateway.support;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import lombok.experimental.UtilityClass;
import org.lan.iti.cloud.gateway.vo.RouteDefinitionVo;

import java.util.ArrayList;
import java.util.List;

/**
 * 路由缓存工具
 *
 * @author NorthLan
 * @date 2021-03-10
 * @url https://noahlan.com
 */
@UtilityClass
public class RouteCacheHolder {
    private Cache<String, RouteDefinitionVo> CACHE = CacheUtil.newLFUCache(50);

    /**
     * 获取缓存的全部对象
     *
     * @return routeList
     */
    public List<RouteDefinitionVo> getRouteList() {
        List<RouteDefinitionVo> routeList = new ArrayList<>();
        CACHE.forEach(routeList::add);
        return routeList;
    }

    /**
     * 更新缓存
     *
     * @param routeList 缓存列表
     */
    public void setRouteList(List<RouteDefinitionVo> routeList) {
        routeList.forEach(route -> CACHE.put(route.getId(), route));
    }

    /**
     * 清空缓存
     */
    public void removeRouteList() {
        CACHE.clear();
    }
}
