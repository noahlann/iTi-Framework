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

package org.lan.iti.common.scanner.model;

import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.ApiModel;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

/**
 * 资源Model
 *
 * @author NorthLan
 * @date 2020-02-20
 * @url https://noahlan.com
 */
@Getter
@ApiModel("资源模型")
@Builder(access = AccessLevel.PRIVATE)
public class ResourceDefinition implements Serializable {
    private static final long serialVersionUID = 3321768686717275759L;

    /**
     * 资源所属应用 标识
     * spring.application.name
     */
    private String applicationName;

    /**
     * 资源所属应用名
     */
    private String serviceName;

    /**
     * 控制器 代码
     * controller code
     */
    private String ctrCode;

    /**
     * 控制器  名
     */
    private String ctrName;

    /**
     * controller 类名
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;

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
    @Builder.Default
    private String httpMethod = "GET";

    /**
     * 是否需要鉴权
     */
    @Builder.Default
    private Boolean requiredPermission = false;

    /**
     * 初始化资源的机器IP
     */
    private String ipAddress;

    @Builder.Default
    private final String codeSeparator = "$";

    public static ResourceDefinition create(String codeSeparator,
                                            String applicationName,
                                            String serviceName,
                                            String ctrCode,
                                            String ctrName,
                                            String className,
                                            String methodName,
                                            String code,
                                            String name,
                                            String url,
                                            String httpMethod,
                                            String ipAddress,
                                            boolean requiredPermission) {
        ResourceDefinitionBuilder builder = ResourceDefinition.builder()
                .codeSeparator(codeSeparator)
                .applicationName(applicationName)
                .serviceName(serviceName)
                .ctrCode(ctrCode)
                .ctrName(ctrName)
                .className(className)
                .methodName(methodName)
                .name(name)
                .url(url)
                .httpMethod(httpMethod)
                .ipAddress(ipAddress)
                .requiredPermission(requiredPermission);
        // code 构造
        String resCode;
        if (StrUtil.isNotBlank(methodName) && StrUtil.isNotBlank(code)) {
            resCode = StrUtil.join(codeSeparator, applicationName, ctrCode, code);
        } else {
            resCode = StrUtil.join(codeSeparator, applicationName, ctrCode);
        }
        builder.code(resCode);
        return builder.build();
    }
}
