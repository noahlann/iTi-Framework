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

package org.lan.iti.cloud.security.properties;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.lan.iti.common.core.util.LambdaUtils;
import org.lan.iti.iha.oauth2.OAuth2Config;
import org.lan.iti.iha.oidc.OidcConfig;
import org.lan.iti.iha.security.jwt.JwtConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Security配置
 *
 * @author NorthLan
 * @date 2021/9/23
 * @url https://blog.noahlan.com
 */
@Data
@ConfigurationProperties(SecurityProperties.PREFIX)
public class SecurityProperties {
    public static final String PREFIX = "iti.security";

    /**
     * OAuth2 配置
     */
    @NestedConfigurationProperty
    private OAuth2Config oauth2 = new OAuth2Config();

    /**
     * Oidc 配置
     */
    @NestedConfigurationProperty
    private OidcConfig oidc = new OidcConfig();

    /**
     * jwt 配置
     */
    @NestedConfigurationProperty
    private JwtConfig jwt = new JwtConfig();

    /**
     * 缓存前缀配置
     */
    @NestedConfigurationProperty
    private CacheConfig cache = new CacheConfig();

    /**
     * 登录成功后跳转到的前端地址
     */
    private String frontUri;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private volatile boolean initOidc = false;

    /**
     * 自定义获取Oidc配置方法，与oauth2重复部分直接使用oauth2的配置
     */
    public OidcConfig getOidc() {
        if (initOidc) {
            return this.oidc;
        }
        BeanUtil.copyProperties(oauth2, oidc, CopyOptions.create().ignoreNullValue()
                .setIgnoreProperties(
                        LambdaUtils.getFieldName(OAuth2Config::getRedirectUri),
                        LambdaUtils.getFieldName(OAuth2Config::getScope)));
        initOidc = true;
        return this.oidc;
    }
}
