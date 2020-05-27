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
     * iTi自定义 鉴权参数前缀
     */
    public static final String AUTH_KEY = "c_auth:";

    /**
     * 校验验证码的客户端
     */
    public static final String NEED_VALIDATE = "nv";

    /**
     * 验证码key
     */
    public static final String CODE = "code";

    /**
     * 自定义鉴权参数缓存键
     */
    public static final String CODE_MOBILE = "mobile";
    public static final String MOBILE_CODE_PREFIX = AUTH_KEY + CODE_MOBILE + ":" + CODE + ":";
    // 需要校验验证码的手机号码列表(set)
    public static final String MOBILE_CODE_NEED_VALIDATE = MOBILE_CODE_PREFIX + NEED_VALIDATE;

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
