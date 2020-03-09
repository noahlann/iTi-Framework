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

package org.lan.iti.common.gateway.configuration;

import org.lan.iti.common.gateway.annotation.EnableITIDynamicRoute;
import org.lan.iti.common.gateway.annotation.enums.DynamicType;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;

import java.util.Map;

/**
 * 动态路由方案选择器
 *
 * @author NorthLan
 * @date 2020-03-09
 * @url https://noahlan.com
 */
public class DynamicRouteConfigurationSelector implements ImportSelector {

    @Override
    @NonNull
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(EnableITIDynamicRoute.class.getName(), true);
        if (attributes == null) {
            return new String[0];
        }
        DynamicType type = (DynamicType) attributes.getOrDefault("value", DynamicType.NACOS_CONFIG);
        return new String[]{type.getConfigClass().getName()};
    }
}
