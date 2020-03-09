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

import lombok.AllArgsConstructor;
import lombok.val;
import org.lan.iti.common.scanner.properties.ScannerProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

/**
 * 扫描器
 *
 * @author NorthLan
 * @date 2020-03-08
 * @url https://noahlan.com
 */
@AllArgsConstructor
public class Scanner implements ApplicationListener<ApplicationReadyEvent> {
    private final ScannerProperties properties;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        String applicationName = applicationContext.getApplicationName();
        //
        RequestMappingHandlerMapping mapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = mapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> it : handlerMethodMap.entrySet()) {
            RequestMappingInfo mappingInfo = it.getKey();
            HandlerMethod handlerMethod = it.getValue();
            // 获取接口对应的class与method
            // handlerMethod.getBeanType()
        }
    }
}
