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

package org.lan.iti.cloud.security.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * OAuthClientDetails 统一模型
 *
 * @author NorthLan
 * @date 2021-03-10
 * @url https://noahlan.com
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode
@ToString
@ApiModel("客户端信息")
public class OauthClientDetails {

    /**
     * 协议名称
     */
    @ApiModelProperty("协议名称")
    private String name;

    /**
     * 客户端标识
     */
    @NotBlank(message = "客户端标识不能为空")
    @ApiModelProperty("客户端标识")
    private String clientId;

    /**
     * 客户端密钥
     */
    @NotBlank(message = "客户端密钥不能为空")
    @ApiModelProperty("客户端密钥")
    private String clientSecret;

    /**
     * 资源ID
     */
    @ApiModelProperty(value = "资源ID列表")
    private String resourceIds;

    /**
     * 作用域
     */
    @NotBlank(message = "作用域不能为空")
    @ApiModelProperty(value = "作用域")
    private String scope;

    /**
     * 授权方式
     * <p>
     * [A,B,C]
     */
    @ApiModelProperty(value = "授权方式")
    private String[] authorizedGrantTypes;

    /**
     * 回调地址
     * <p>
     * [A,B,C]
     */
    @ApiModelProperty(value = "回调地址")
    private String webServerRedirectUri;

    /**
     * 权限列表
     */
    @ApiModelProperty(value = "权限列表")
    private String authorities;

    /**
     * 请求令牌有效期,单位: 秒
     */
    @ApiModelProperty(value = "请求令牌有效时间")
    private Integer accessTokenValidity;

    /**
     * 刷新令牌有效期,单位: 秒
     */
    @ApiModelProperty(value = "刷新令牌有效时间")
    private Integer refreshTokenValidity;

    /**
     * 扩展信息
     */
    @ApiModelProperty(value = "扩展信息")
    private String additionalInformation;

    /**
     * 自动放行作用域列表
     * <p>
     * [A,B,C]
     */
    @ApiModelProperty(value = "是否自动放行")
    private String autoapprove;
}
