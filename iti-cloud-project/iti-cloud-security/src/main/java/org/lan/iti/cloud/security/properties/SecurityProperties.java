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
import org.springframework.boot.web.servlet.DispatcherType;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.core.Ordered;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
     * Order applied to the WebSecurityConfigurerAdapter that is used to configure basic
     * authentication for application endpoints. If you want to add your own
     * authentication for all or some of those endpoints the best thing to do is to add
     * your own WebSecurityConfigurerAdapter with lower order.
     */
    public static final int BASIC_AUTH_ORDER = Ordered.LOWEST_PRECEDENCE - 5;

    /**
     * Order applied to the WebSecurityConfigurer that ignores standard static resource
     * paths.
     */
    public static final int IGNORED_ORDER = Ordered.HIGHEST_PRECEDENCE;

    /**
     * Default order of Spring Security's Filter in the servlet container (i.e. amongst
     * other filters registered with the container). There is no connection between this
     * and the {@code @Order} on a WebSecurityConfigurer.
     */
    public static final int DEFAULT_FILTER_ORDER = OrderedFilter.REQUEST_WRAPPER_FILTER_MAX_ORDER - 100;

    @Getter
    @Setter(AccessLevel.NONE)
    private final Filter filter = new Filter();

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

    public static class Filter {

        /**
         * Security filter chain order.
         */
        private int order = DEFAULT_FILTER_ORDER;

        /**
         * Security filter chain dispatcher types.
         */
        private Set<DispatcherType> dispatcherTypes = new HashSet<>(
                Arrays.asList(DispatcherType.ASYNC, DispatcherType.ERROR, DispatcherType.REQUEST));

        public int getOrder() {
            return this.order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public Set<DispatcherType> getDispatcherTypes() {
            return this.dispatcherTypes;
        }

        public void setDispatcherTypes(Set<DispatcherType> dispatcherTypes) {
            this.dispatcherTypes = dispatcherTypes;
        }

    }
}
