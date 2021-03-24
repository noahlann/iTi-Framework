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

package org.lan.iti.cloud.swagger.plugins;

import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import springfox.documentation.builders.ModelSpecificationBuilder;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.util.Optional;

/**
 * LongToString转换插件
 * <p>由于long数据传输精度问题,框架已将Long当做string处理</p>
 * <p>该插件的目的亦是如此，将Long当做string处理</p>
 *
 * @author NorthLan
 * @date 2021-03-22
 * @url https://noahlan.com
 */
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 1000)
public class LongToStringPlugin implements ModelPropertyBuilderPlugin {

    @Override
    public void apply(ModelPropertyContext context) {
        Optional<BeanPropertyDefinition> optional = context.getBeanPropertyDefinition();
        if (!optional.isPresent()) {
            return;
        }

        final Class<?> fieldType = optional.get().getField().getRawType();

        addDescriptionForLong(context, fieldType);
    }

    @Override
    public boolean supports(@NonNull DocumentationType delimiter) {
        return SwaggerPluginSupport.pluginDoesApply(delimiter);
    }

    private void addDescriptionForLong(ModelPropertyContext context, Class<?> fieldType) {
        if (Long.class.equals(fieldType)) {
            context.getSpecificationBuilder()
                    .type(new ModelSpecificationBuilder()
                            .scalarModel(ScalarType.STRING)
                            .build());
        }
    }
}
