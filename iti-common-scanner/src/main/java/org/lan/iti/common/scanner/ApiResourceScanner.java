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

package org.lan.iti.common.scanner;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.util.AopUtils;
import org.lan.iti.common.scanner.annotation.ITIApi;
import org.lan.iti.common.scanner.model.ResourceDefinition;
import org.lan.iti.common.scanner.properties.ScannerProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资源扫描器,仅扫描注解了 @ITIApi 的 Controller 方法
 * <p>
 * ApplicationContextAware先于BeanPostProcessor执行
 * </p>
 *
 * @author NorthLan
 * @date 2020-03-05
 * @url https://noahlan.com
 */
@Slf4j
public class ApiResourceScanner implements BeanPostProcessor, ApplicationContextAware {
    private String applicationName;

    private final ScannerProperties properties;

    public ApiResourceScanner(ScannerProperties properties) {
        this.properties = properties;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationName = applicationContext.getApplicationName();
        if (StrUtil.isBlank(applicationName)) {
            log.error("服务名为空,请检查是否填写了配置[spring.application.name]");
        }
    }

    @Override
    public Object postProcessBeforeInitialization(@Nullable Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(@Nullable Object bean, String beanName) throws BeansException {
        if (bean == null) {
            return null;
        }
        Object aopTarget = AopUtils.getTarget(bean);
        if (aopTarget == null) {
            aopTarget = bean;
        }
        Class<?> clazz = aopTarget.getClass();

        // RestController | Controller && ITIApi
        if (!isApiController(clazz)) {
            return bean;
        }

        // 扫描并注册
        scan(clazz);

        return bean;
    }

    /**
     * 判断一个bean是否是 Controller | RestController && ApiResource
     *
     * @param clazz bean类型
     */
    private boolean isApiController(Class<?> clazz) {
        boolean isController = false;
        boolean isApi = false;

        for (Annotation it : clazz.getAnnotations()) {
            Class<? extends Annotation> annotationType = it.annotationType();
            if (ITIApi.class == annotationType) {
                isApi = true;
            }
            if (RestController.class == annotationType || Controller.class == annotationType) {
                isController = true;
                break;
            }
        }
        return isController && isApi;
    }

    /**
     * 扫描并注册
     * <pre>
     *     注解置于控制器时，为控制器全局注解
     *     优先级（除了code与name外）：控制器 > 方法
     * </pre>
     *
     * @param clazz 待扫描类
     */
    private void scan(Class<?> clazz) {
        List<ResourceDefinition> result = new ArrayList<>();

        String ctrCode = getCtrShortName(clazz.getSimpleName());
        String ctrName = clazz.getSimpleName();
        boolean requiredPermission;
        ITIApi ctrAnnotation = clazz.getAnnotation(ITIApi.class);
        if (ctrAnnotation != null) {
            if (StrUtil.isNotBlank(ctrAnnotation.code())) {
                ctrCode = ctrAnnotation.code();
            }
            if (StrUtil.isNotBlank(ctrAnnotation.name())) {
                ctrName = ctrAnnotation.name();
            }
            requiredPermission = ctrAnnotation.requiredPermission();
        } else {
            // 控制器不存在注解,不注入资源
            return;
        }
        // 资源字典
        ApiResourceHolder.bindDict(ctrCode, ctrName);

        // 控制器根资源,指代此控制器所有权限
        ResourceDefinition ctrResourceDefinition = buildCtrResource(clazz, ctrCode, ctrName, requiredPermission);
        log.debug("扫描到控制器资源: {}", ctrResourceDefinition);
        result.add(ctrResourceDefinition);

        // 控制器接口资源
        for (Method it : clazz.getDeclaredMethods()) {
            ResourceDefinition resourceDefinition = buildResource();
            if (resourceDefinition != null) {
                result.add(resourceDefinition);
                log.debug("扫描到资源: {}", resourceDefinition);
            }
        }

        // 注册
        ApiResourceHolder.register(result);
    }

    // region resource definition
    private ResourceDefinition buildCtrResource(Class<?> clazz, String ctrCode, String ctrName, boolean requiredPermission) {
        // url - httpMethod
        String httpMethod = Arrays.stream(RequestMethod.values()).map(RequestMethod::name).collect(Collectors.joining(","));
        return null;
    }

    private ResourceDefinition buildResource() {
        return null;
    }
    // endregion

    // region string functions

    /**
     * 获取控制器短名称
     * <p>xxxController的xxx</p>
     *
     * @param fullName 控制器全称
     */
    private String getCtrShortName(String fullName) {
        int ctrIdx = fullName.indexOf("Controller");
        if (ctrIdx == -1) {
            // 控制器名称不规范，给出提示
            log.warn("控制器：{} 名称不规范，应以'Controller'结尾！", fullName);
            return fullName;
        }
        return fullName.substring(0, ctrIdx);
    }

    /**
     * 获取通配符形式URL
     * <p>
     * RequestMapping(/test/) 匹配为 /test/**
     * </p>
     *
     * @param clazz 控制器类
     */
    private String getCtrWildCardUrl(Class<?> clazz) {
        return "";
    }

    private String getCtrUrl(Class<?> clazz) {
        RequestMapping requestMapping = clazz.getDeclaredAnnotation(RequestMapping.class);
        if (requestMapping == null) {
            log.error("控制器: {} 未指定@RequestMapping,请检查", clazz.getName());
            return null;
        }
        return requestMapping.value()[0];
    }
    // endregion
}
