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

package org.lan.iti.cloud.resscanner;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.lan.iti.cloud.util.BeanUtils;
import org.lan.iti.cloud.util.PropertiesUtils;
import org.lan.iti.cloud.resscanner.annotation.ITIApi;
import org.lan.iti.cloud.resscanner.exception.ScannerException;
import org.lan.iti.cloud.resscanner.feign.RemoteResourceService;
import org.lan.iti.cloud.resscanner.model.ResourceDefinition;
import org.lan.iti.cloud.resscanner.properties.ScannerProperties;
import org.lan.iti.cloud.resscanner.util.CodeUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * 扫描器
 *
 * @author NorthLan
 * @date 2020-03-08
 * @url https://noahlan.com
 */
@Slf4j
public class ResourceScanner implements SmartApplicationListener {
    private final ScannerProperties properties;
    //
    private boolean swaggerAvailable = false;
    private ApplicationContext applicationContext;
    private String applicationName;
    private String ipAddress;

    public ResourceScanner(ScannerProperties properties) {
        this.properties = properties;
    }

    private static final Map<Class<?>, ResourceDefinition> ctrResourceMap = new HashMap<>();
    private static final String DELIMETER = ",";

    @Override
    public boolean supportsEventType(@NonNull Class<? extends ApplicationEvent> eventType) {
        return eventType == ApplicationReadyEvent.class;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    @Override
    public void onApplicationEvent(@NonNull ApplicationEvent event) {
        this.swaggerAvailable = isSwaggerAvailable();
        this.ipAddress = NetUtil.getLocalhostStr();
        this.applicationContext = ((ApplicationReadyEvent) event).getApplicationContext();
        this.applicationName = PropertiesUtils.getOrDefault("spring.application.name", "unknown");
        // scanning
        scan();
        // reporting
        report();
    }

    private void scan() {
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
        for (RequestMappingInfo info : map.keySet()) {
            HandlerMethod handlerMethod = map.get(info);
            Class<?> ctrClass = handlerMethod.getBeanType();
            // method 与 controller 至少存在一个
            if (AnnotationUtils.findAnnotation(ctrClass, ITIApi.class) == null && AnnotationUtils.findAnnotation(handlerMethod.getMethod(), ITIApi.class) == null) {
                continue;
            }
            // controller api
            ResourceDefinition ctrResource;
            try {
                ctrResource = ctrResourceMap.computeIfAbsent(ctrClass, buildCtrResource());
            } catch (ScannerException | IllegalArgumentException e) {
                log.error(e.getMessage());
                continue;
            }

            // method api
            ResourceDefinition apiResource;
            try {
                apiResource = buildResource(info, ctrResource, handlerMethod);
            } catch (ScannerException | IllegalArgumentException e) {
                log.error(e.getMessage());
                continue;
            }
            if (apiResource == null) {
                continue;
            }

            // 注册资源
            ResourceCache.register(apiResource);
        }
    }

    private void report() {
        if (!properties.getReport().isEnabled()) {
            log.info("未开启资源上报功能,跳过...");
            return;
        }

        RemoteResourceService resourceService;
        try {
            resourceService = applicationContext.getBean(RemoteResourceService.class);
        } catch (BeansException e) {
            log.error("未发现资源上报服务,请检查配置启动顺序", e);
            return;
        }

        // 本服务所有资源
        val serviceResources = ResourceCache.getAllResources();
        if (!serviceResources.isEmpty()) {
            log.info("上报所有资源定义到上游服务...");
            try {
                resourceService.reportResources(applicationName, serviceResources);
            } catch (Exception e) {
                // 不打印堆栈信息
                log.error("上报资源失败，发生异常: {}", e.getMessage());
                return;
            }
            log.info("上报所有资源定义到上游服务成功,上报总量: {}控制器 {}接口", serviceResources.size(), ResourceCache.getAllResources().size());
        } else {
            log.info("服务中无开放接口资源,无需上报...");
        }
    }
    // region build

    /**
     * 构建API资源
     *
     * @param info          请求信息
     * @param ctrResource   控制器资源
     * @param handlerMethod 方法监管
     * @return API资源对象
     */
    private ResourceDefinition buildResource(RequestMappingInfo info, ResourceDefinition ctrResource, HandlerMethod handlerMethod) {
        Method method = handlerMethod.getMethod();
        ITIApi api = AnnotationUtils.findAnnotation(method, ITIApi.class);
        if (api == null) {
            return null;
        }
        ResourceDefinition resource = BeanUtils.convert(ctrResource, ResourceDefinition.class);
        String code = api.code();
        String name = api.name();
        if (StrUtil.isBlank(code)) {
            code = method.getName();
        }
        // swagger 支持
        if (StrUtil.isBlank(name) && swaggerAvailable) {
            ApiOperation swaggerApi = AnnotationUtils.findAnnotation(method, ApiOperation.class);
            if (swaggerApi != null && StrUtil.isNotBlank(swaggerApi.value())) {
                name = swaggerApi.value();
            }
        }
        if (StrUtil.isBlank(name)) {
            name = code;
        }

        resource.setCode(CodeUtils.getResourceCode(properties.getDelimiter(), applicationName, resource.getModuleCode(), code))
                .setName(name);

        Set<String> pattern = info.getPatternsCondition().getPatterns();
        Assert.notEmpty(pattern, "无法获取API URL");

        Set<RequestMethod> methods = info.getMethodsCondition().getMethods();
        Assert.notEmpty(pattern, "无法获取RequestMethod");
        resource.setUrl(pattern.iterator().next())
                .setHttpMethod(CollUtil.join(methods, DELIMETER));
        return resource;
    }

    /**
     * 构建控制器方法
     * Class<?>为控制器Class
     *
     * @return 控制器资源对象
     */
    private Function<Class<?>, ResourceDefinition> buildCtrResource() {
        return clazz -> {
            ITIApi api = AnnotationUtils.findAnnotation(clazz, ITIApi.class);
            ResourceDefinition resource = new ResourceDefinition();

            String moduleName = null;
            String moduleCode = null;
            if (api != null) {
                moduleName = api.name();
                moduleCode = api.code();
            }
            if (StrUtil.isBlank(moduleCode)) {
                moduleCode = CodeUtils.getCtrShortName(clazz, properties.getCtrSuffix());
            }
            // swagger支持
            if (StrUtil.isBlank(moduleName) && swaggerAvailable) {
                Api swaggerApi = AnnotationUtils.findAnnotation(clazz, Api.class);
                if (swaggerApi != null) {
                    // 优先读取tags
                    String[] tags = swaggerApi.tags();
                    if (ArrayUtil.isNotEmpty(tags)) {
                        // join with ,
                        moduleName = ArrayUtil.join(tags, DELIMETER);
                    } else if (StrUtil.isNotBlank(swaggerApi.value())) {
                        // 取value
                        moduleName = swaggerApi.value();
                    }
                }
            }
            if (StrUtil.isBlank(moduleName)) {
                moduleName = moduleCode;
            }

            resource.setModuleCode(moduleCode)
                    .setModuleName(moduleName)
                    .setCode(CodeUtils.getResourceCode(properties.getDelimiter(), applicationName, moduleCode, null))
                    .setName(moduleName);
            resource.setServiceCode(applicationName)
                    .setServiceName(properties.getServiceName());

            resource.setIpAddress(ipAddress);
            resource.setHttpMethod(null);

            // register
            ResourceCache.register(resource);
            return resource;
        };
    }
    // endregion

    // region inner helper
    private boolean isSwaggerAvailable() {
        try {
            Class.forName("io.swagger.annotations.Api");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    // endregion
}
