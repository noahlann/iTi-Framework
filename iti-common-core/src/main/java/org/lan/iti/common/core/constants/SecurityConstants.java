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
 * Security 常量
 *
 * @author NorthLan
 * @date 2020-02-20
 * @url https://noahlan.com
 * TODO 取消很多无用的constants
 */
public final class SecurityConstants {
    /**
     * 验证码有效期(秒)
     */
    public static final Long CODE_TIME = 300L;

    /**
     * 验证码长度
     */
    public static final Integer CODE_SIZE = 4;

    /**
     * Basic 前缀
     */
    public static final String BASIC_ = "Basic ";

    /**
     * 角色前缀
     */
    public static final String ROLE = "ROLE_";

    /**
     * 资源前缀
     */
    public static final String PREFIX_AUZ = "ITI_AUZ_";

    /**
     * 系统前缀
     */
    public static final String PREFIX = "iti_";

    /**
     * oauth 相关前缀
     */
    public static final String OAUTH_PREFIX = "oauth:";

    /**
     * 项目的license
     */
    public static final String LICENSE = "made by northlan";

    /**
     * 刷新
     */
    public static final String REFRESH_TOKEN = "refresh_token";

    /**
     * 内部
     */
    public static final String FROM_IN = "Y";

    /**
     * 标志
     */
    public static final String FROM = "X-FROM";

    /**
     * 内部标志 Header 表示
     */
    public static final String HEADER_FROM_IN = FROM + ": " + FROM_IN;

    /**
     * domain
     */
    public static final String DOMAIN = "domain";

    /**
     * 默认用户域
     */
    public static final String DEFAULT_DOMAIN = "sys";

    /**
     * 默认服务提供商
     */
    public static final String DEFAULT_PROVIDER_ID = "sys";

    /**
     * oauth获取token接口URL
     */
    public static final String OAUTH_TOKEN_URL = "/oauth/token";

    /**
     * {bcrypt} 加密的特征码
     */
    public static final String BCRYPT = "{bcrypt}";

    /**
     * 微信获取OPENID
     */
    public static final String WECHAT_AUTHORIZATION_CODE_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
            "appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    /**
     * 微信小程序OPENID
     */
    public static final String WECHAT_MINI_APP_AUTHORIZATION_CODE_URL = "https://api.weixin.qq.com/sns/jscode2session?" +
            "appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    /**
     * 码云获取token
     */
    public static final String GITEE_AUTHORIZATION_CODE_URL = "https://gitee.com/oauth/token?grant_type=" +
            "authorization_code&code=%S&client_id=%s&redirect_uri=" +
            "%s&client_secret=%s";

    /**
     * 开源中国获取token
     */
    public static final String OSC_AUTHORIZATION_CODE_URL = "https://www.oschina.net/action/openapi/token";

    /**
     * 码云获取用户信息
     */
    public static final String GITEE_USER_INFO_URL = "https://gitee.com/api/v5/user?access_token=%s";

    /**
     * 开源中国用户信息
     */
    public static final String OSC_USER_INFO_URL = "https://www.oschina.net/action/openapi/user?access_token=%s&dataType=json";

    /**
     * 资源服务器默认bean名称
     */
    public static final String RESOURCE_SERVER_CONFIGURER = "resourceServerConfigurerAdapter";

    /**
     * 客户端模式
     */
    public static final String CLIENT_CREDENTIALS = "client_credentials";

    /**
     * 默认用户ID(未登录用户)
     */
    public static final String DEFAULT_USER_ID = "0";

    // region token 字段
    /**
     * 租户ID 字段
     */
    public static final String DETAILS_TENANT_ID = "tenant_id";

    /**
     * 用户信息
     */
    public static final String DETAILS_USER_DETAILS = "user_details";

    /**
     * 协议字段
     */
    public static final String DETAILS_LICENSE = "license";

    /**
     * 激活字段 兼容外围系统接入
     */
    public static final String ACTIVE = "active";
    // endregion
}