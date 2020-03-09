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

package org.lan.iti.common.scanner.listener;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.lan.iti.common.scanner.ApiResourceHolder;
import org.lan.iti.common.scanner.feign.RemoteResourceService;
import org.lan.iti.common.scanner.properties.ScannerProperties;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

/**
 * 服务初始化完成，上报本服务资源
 *
 * @author NorthLan
 * @date 2020-03-06
 * @url https://noahlan.com
 */
@Slf4j
public class ResourceReportListener implements ApplicationListener<ApplicationReadyEvent> {

    @NotNull
    private ScannerProperties properties;

    public ResourceReportListener(ScannerProperties properties) {
        this.properties = properties;
    }

    @Override
    public void onApplicationEvent(@Nullable ApplicationReadyEvent applicationReadyEvent) {
        // 不采用Assert形式,仅给出ERROR级别日志,上报可不执行但应用不能出错
        if (!properties.getReport().isEnabled()) {
            log.info("未开启资源上报功能,跳过...");
            return;
        }

        if (applicationReadyEvent == null) {
            log.error("applicationReadyEvent未正确注册,请检查启动顺序！");
            return;
        }

        // applicationContext
        ApplicationContext applicationContext = applicationReadyEvent.getApplicationContext();
        if (applicationContext == null) {
            log.error("applicationContext未正确注册,请检查启动顺序！");
            return;
        }

        // RemoteResourceService
        RemoteResourceService resourceService = null;
        try {
            resourceService = applicationContext.getBean(RemoteResourceService.class);
        } catch (BeansException e) {
            // just catch
        }
        if (resourceService == null) {
            log.error("未发现资源报告服务[RemoteResourceService],请检查是否正确注入(Feign)");
            return;
        }

        // applicationName(spring.application.name)
        String applicationName = applicationContext.getApplicationName();
        if (StrUtil.isBlank(applicationName)) {
            log.error("服务名为空,请检查是否填写了配置[spring.application.name]");
            return;
        }



        // 本服务所有资源
        val serviceResources = ApiResourceHolder.getCtrResources();
        if (!serviceResources.isEmpty()) {
            log.info("上报所有资源定义到上游服务...");
            resourceService.reportResources(applicationName, serviceResources);
            log.info("上报所有资源定义到上游服务成功,上报总量: {}", serviceResources.size());
        } else {
            log.info("服务中无开放接口资源,无需上报...");
        }
    }
}
