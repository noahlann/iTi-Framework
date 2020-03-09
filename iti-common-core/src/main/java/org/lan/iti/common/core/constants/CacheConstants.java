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

package org.lan.iti.common.core.constants;


/**
 * 缓存常量
 *
 * @author NorthLan
 * @date 2020-02-20
 * @url https://noahlan.com
 */
public final class CacheConstants {
    /**
     * 全局缓存Key
     * 忽略租户信息
     */
    public static final String GLOBAL_KEY = "GLOBAL:";

    /**
     * 验证码前缀
     */
    public static final String DEFAULT_CODE_KEY = "DEFAULT_CODE_KEY:";

    /**
     * 用户信息缓存
     */
    public static final String USER_DETAILS = "user_details";

    /**
     * 菜单信息缓存
     */
    public static final String MENU_DETAILS = "menu_details";

    /**
     * 资源信息缓存
     */
    public static final String RESOURCE_DETAILS = "resource_details";

    /**
     * 字典缓存
     */
    public static final String DICT_DETAILS = "dict_details";

    /**
     * 参数缓存
     */
    public static final String PARAMS_DETAILS = "params_details";

    /**
     * 租户缓存
     * 不区分租户
     */
    public static final String TENANT_DETAILS = GLOBAL_KEY + "tenant_details";

    /**
     * oauth 客户端信息 key
     */
    public static final String CLIENT_DETAILS_KEY = SecurityConstants.PREFIX + SecurityConstants.OAUTH_PREFIX + "client:details";

    /**
     * 路由存放
     */
    public static final String ROUTE_KEY = "gateway_route_key";

    /**
     * redis reload 事件
     */
    public static final String ROUTE_REDIS_RELOAD_TOPIC = "gateway_redis_route_reload_topic";

    /**
     * 内存reload 时间
     */
    public static final String ROUTE_JVM_RELOAD_TOPIC = "gateway_jvm_route_reload_topic";

    /**
     * spring boot admin 事件key
     */
    public static final String EVENT_KEY = "event_key";
}
