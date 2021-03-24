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
public interface SecurityConstants {

    /**
     * 验证码有效期(秒)
     */
    Long CODE_TIME = 300L;

    /**
     * 验证码长度
     */
    Integer CODE_SIZE = 4;

    /**
     * Basic 前缀
     */
    String BASIC_ = "Basic ";

    /**
     * 角色前缀
     */
    String ROLE = "ROLE_";

    /**
     * 资源前缀
     */
    String PREFIX_AUZ = "ITI_AUZ_";

    /**
     * 系统前缀
     */
    String PREFIX = "iti_";

    /**
     * oauth 相关前缀
     */
    String OAUTH_PREFIX = "oauth:";

    /**
     * oauth 授权码模式 code 的前缀
     */
    String OAUTH_CODE_PREFIX = OAUTH_PREFIX + "code:";

    /**
     * 项目的license
     */
    String LICENSE = "made by northlan";

    /**
     * 刷新
     */
    String REFRESH_TOKEN = "refresh_token";

    /**
     * 内部
     */
    String FROM_IN = "Y";

    /**
     * 标志
     */
    String FROM = "X-FROM";

    /**
     * 内部标志 Header 表示
     */
    String HEADER_FROM_IN = FROM + ": " + FROM_IN;

    /**
     * domain
     */
    String DOMAIN = "domain";

    /**
     * 默认用户域
     */
    String DEFAULT_DOMAIN = "sys";

    /**
     * 默认服务提供商
     */
    String DEFAULT_PROVIDER_ID = "sys";

    /**
     * oauth获取token接口URL
     */
    String OAUTH_TOKEN_URL = "/oauth/token";

    /**
     * {bcrypt} 加密的特征码
     */
    String BCRYPT = "{bcrypt}";

    /**
     * 微信获取OPENID
     */
    String WECHAT_AUTHORIZATION_CODE_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
            "appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    /**
     * 微信小程序OPENID
     */
    String WECHAT_MINI_APP_AUTHORIZATION_CODE_URL = "https://api.weixin.qq.com/sns/jscode2session?" +
            "appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

    /**
     * 码云获取token
     */
    String GITEE_AUTHORIZATION_CODE_URL = "https://gitee.com/oauth/token?grant_type=" +
            "authorization_code&code=%S&client_id=%s&redirect_uri=" +
            "%s&client_secret=%s";

    /**
     * 开源中国获取token
     */
    String OSC_AUTHORIZATION_CODE_URL = "https://www.oschina.net/action/openapi/token";

    /**
     * 码云获取用户信息
     */
    String GITEE_USER_INFO_URL = "https://gitee.com/api/v5/user?access_token=%s";

    /**
     * 开源中国用户信息
     */
    String OSC_USER_INFO_URL = "https://www.oschina.net/action/openapi/user?access_token=%s&dataType=json";

    /**
     * 资源服务器默认bean名称
     */
    String RESOURCE_SERVER_CONFIGURER = "resourceServerConfigurerAdapter";

    /**
     * 资源服务器Token服务默认bean名称
     */
    String RESOURCE_SERVER_TOKEN_SERVICES = "resourceServerTokenServices";

    /**
     * 客户端模式
     */
    String CLIENT_CREDENTIALS = "client_credentials";

    /**
     * 默认用户ID(未登录用户)
     */
    String DEFAULT_USER_ID = "0";

    // region token 字段
    /**
     * 租户ID 字段
     */
    String DETAILS_TENANT_ID = "tenant_id";

    /**
     * 用户信息
     */
    String DETAILS_USER_DETAILS = "user_details";

    /**
     * 协议字段
     */
    String DETAILS_LICENSE = "license";

    /**
     * 激活字段 兼容外围系统接入
     */
    String ACTIVE = "active";
    // endregion
}
