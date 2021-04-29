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

package org.lan.iti.cloud.resscanner.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 资源Model
 *
 * @author NorthLan
 * @date 2020-02-20
 * @url https://noahlan.com
 */
@Data
@Accessors(chain = true)
@ApiModel("资源模型")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceDefinition implements Serializable {
    private static final long serialVersionUID = 3321768686717275759L;

    /**
     * 所属服务标识
     * spring.application.name
     */
    private String serviceCode;

    /**
     * 所属服务名
     */
    private String serviceName;

    /**
     * 模块代码
     */
    private String moduleCode;

    /**
     * 模块名
     */
    private String moduleName;

    /**
     * 资源标识
     * 接口  ：applicationName$ctrCode&code|methodName
     * 控制器：applicationName&ctrCode
     */
    private String code;

    /**
     * 资源名称
     */
    private String name;

    /**
     * 资源请求路径
     */
    private String url;

    /**
     * 资源请求方式
     */
    private String httpMethod;

    /**
     * 初始化资源的机器IP
     */
    private String ipAddress;

    /**
     * 资源类型
     */
    private String type;
}
