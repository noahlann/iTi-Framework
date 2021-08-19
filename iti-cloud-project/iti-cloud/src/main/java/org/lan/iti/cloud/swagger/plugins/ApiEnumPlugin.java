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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.classmate.ResolvedType;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.swagger.annotation.ApiEnum;
import org.lan.iti.common.core.support.IEnum;
import org.lan.iti.common.core.util.StringPool;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.builders.PropertySpecificationBuilder;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.AllowableListValues;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin;
import springfox.documentation.spi.schema.contexts.ModelPropertyContext;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

/**
 * ApiEnum 扩展插件
 * <p>为枚举类型添加注解@ApiEnum，用于给出文档描述</p>
 * <pre>
 *     1. 参数使用自定义对象 不添加注解 -> OperationContext （OperationBuilder中具有拆分的Parameter信息）
 *     2. 参数使用自定义对象 添加@RequestBody -> ModelPropertyContext（拆分）、ParameterContext、OperationContext
 *     3. 参数使用自定义对象 添加@RequestParam -> ParameterContext、OperationContext （用法错误，不处理）
 *     4. 参数直接使用Enum对象 不添加注解 -> ParameterContext、OperationContext
 *     5. 参数直接使用Enum对象 添加@RequestParam -> ParameterContext、OperationContext
 * </pre>
 * <p>再根据源码中的读取逻辑
 * {@link springfox.documentation.spring.web.readers.operation.OperationParameterReader# readParameters}
 * 发现ParameterContext是在读取 parameter 时由 Operator触发
 * 故，仅需处理OperationContext与ModelPropertyContext即可</p>
 *
 * @author NorthLan
 * @date 2021-03-22
 * @url https://noahlan.com
 */
@SuppressWarnings("AlibabaRemoveCommentedCode")
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER + 1000)
@Slf4j
public class ApiEnumPlugin implements ModelPropertyBuilderPlugin, OperationBuilderPlugin {

    @Override
    public void apply(ModelPropertyContext context) {
        // return && @RequestBody
        Optional<BeanPropertyDefinition> optional = context.getBeanPropertyDefinition();
        if (optional.isPresent()) {
            AnnotatedField aField = optional.get().getField();
            if (aField != null) {
                Field field = aField.getAnnotated();
                if (field != null) {
                    Class<?> clazz = field.getType();
                    if (!clazz.isEnum()) {
                        return;
                    }
                    Details details = buildDetails((Class<? extends Enum<?>>) clazz);
                    if (details.isValid()) {
                        PropertySpecificationBuilder builder = context.getSpecificationBuilder();
                        builder.description(details.getDesc())
                                .defaultValue(details.getExample())
                                .enumerationFacet(f -> f.allowedValues(new AllowableListValues(details.getValues(), String.class.getName())));
                    }
                }
            }
        }
    }

    @SuppressWarnings("deprecated")
    @Override
    public void apply(OperationContext context) {
        /* @since 3.0.0
         * springfox 将 parameters 标记为过时并替换为 requestParameters
         * 而 requestParameters 中不包含任何原始类信息,无法使用
         * 故使用反射获取 parameters 后使用
         */
        OperationBuilder operationBuilder = context.operationBuilder();
        Field parametersField = ReflectionUtils.findField(operationBuilder.getClass(), "parameters");
        if (parametersField == null) {
            return;
        }
        ReflectionUtils.makeAccessible(parametersField);
        List<Parameter> parameters = (List<Parameter>) ReflectionUtils.getField(parametersField, operationBuilder);
        if (CollUtil.isEmpty(parameters)) {
            return;
        }
        Set<RequestParameter> requestParameters = new HashSet<>();

        for (Parameter parameter : parameters) {
            Optional<ResolvedType> optional = parameter.getType();
            if (!optional.isPresent()) {
                continue;
            }
            Class<?> clazz = optional.get().getErasedType();
            if (!clazz.isEnum()) {
                continue;
            }
            Details details = buildDetails((Class<? extends Enum<?>>) clazz);
            RequestParameterBuilder builder = new RequestParameterBuilder();
            builder.name(parameter.getName())
                    .in(parameter.getParamType())
                    .description(parameter.getDescription() + "\n" + details.getDesc())
                    .required(parameter.isRequired())
                    .hidden(parameter.isHidden())
                    .precedence(parameter.getOrder())
                    .query(q -> q.defaultValue(details.getExample())
                            .model(m -> m.scalarModel(ScalarType.STRING).name(parameter.getName()))
                            .enumerationFacet(f -> f.allowedValues(details.getValues()))
                    );
            requestParameters.add(builder.build());
        }
        if (CollUtil.isEmpty(requestParameters)) {
            return;
        }
        /*
         * merge会将facets也融合,而我们的目的是替换
         * operationBuilder.requestParameters(requestParameters);
         */
        Field requestParametersField = ReflectionUtils.findField(operationBuilder.getClass(), "requestParameters");
        if (requestParametersField == null) {
            return;
        }
        ReflectionUtils.makeAccessible(requestParametersField);
        Set<RequestParameter> existsParameters = (Set<RequestParameter>) ReflectionUtils.getField(requestParametersField, operationBuilder);
        if (CollUtil.isEmpty(existsParameters)) {
            operationBuilder.requestParameters(requestParameters);
            return;
        }
        RequestParameterReplacer replacer = new RequestParameterReplacer(existsParameters, requestParameters);
        ReflectionUtils.setField(requestParametersField, operationBuilder, replacer.replace());
    }

    @Override
    public boolean supports(@NonNull DocumentationType delimiter) {
        return SwaggerPluginSupport.pluginDoesApply(delimiter);
    }

    private Details buildDetails(Class<? extends Enum<?>> clazz) {
        String valueField = "";
        String descField = "";
        ApiEnum classApiEnum = AnnotationUtils.findAnnotation(clazz, ApiEnum.class);
        if (classApiEnum != null) {
            descField = classApiEnum.descField();
            valueField = classApiEnum.valueField();
        }
        Details result = new Details();
        List<String> lines = new ArrayList<>();
        for (Enum<?> item : clazz.getEnumConstants()) {
            String value = getValue(item, valueField);
            result.addValue(value);
            result.setDefault(value);

            String line = "* " + value + ": " + getDesc(item, descField);
            lines.add(line);
        }
        result.setDesc(CollUtil.join(lines, StringPool.NEWLINE));
        return result;
    }

    private String getValue(Enum<?> item, String valueField) {
        boolean loaded = false;
        String value = "";

        if (IEnum.class.isAssignableFrom(item.getClass())) {
            IEnum<?> iEnum = (IEnum<?>) item;
            value = String.valueOf(iEnum.getCode());
            loaded = true;
        }

        if (StrUtil.isBlank(valueField)) {
            return item.toString();
        }
        Method method = ReflectionUtils.findMethod(item.getClass(), "get" + StrUtil.upperFirst(valueField));
        if (method != null) {
            value = String.valueOf(ReflectionUtils.invokeMethod(method, item));
            loaded = true;
        }

        if (!loaded) {
            Field field = ReflectionUtils.findField(item.getClass(), valueField);
            if (field != null) {
                ReflectionUtils.makeAccessible(field);
                value = String.valueOf(ReflectionUtils.getField(field, item));
                loaded = true;
            }
        }

        return loaded ? value : "Invalid Value, Check code!";
    }

    private String getDesc(Enum<?> item, String descField) {
        boolean loaded = false;
        String desc = "";

        if (IEnum.class.isAssignableFrom(item.getClass())) {
            IEnum<?> iEnum = (IEnum<?>) item;
            desc = iEnum.getMessage();
            loaded = true;
        }

        // 尽可能读取枚举字段上的注解值
        if (!loaded) {
            ApiEnum fieldApiEnum = null;
            try {
                fieldApiEnum = item.getClass().getField((item).name()).getAnnotation(ApiEnum.class);
            } catch (NoSuchFieldException ignore) {
            }
            if (fieldApiEnum != null) {
                if (StrUtil.isNotBlank(fieldApiEnum.desc())) {
                    desc = fieldApiEnum.desc();
                    loaded = true;
                }
            }
        }

        // 读取getter
        if (!loaded && StrUtil.isNotBlank(descField)) {
            Method method = ReflectionUtils.findMethod(item.getClass(), "get" + StrUtil.upperFirst(descField));
            if (method != null) {
                if (String.class.equals(method.getReturnType())) {
                    desc = (String) ReflectionUtils.invokeMethod(method, item);
                    loaded = true;
                }
            }
        }

        // 反射获取field值
        if (!loaded && StrUtil.isNotBlank(descField)) {
            Field field = ReflectionUtils.findField(item.getClass(), descField);
            if (field != null) {
                ReflectionUtils.makeAccessible(field);
                desc = (String) ReflectionUtils.getField(field, item);
                loaded = true;
            }
        }

        return loaded ? desc : "No Descriptions";
    }

    @Getter
    public static final class Details {
        String example;

        @Setter
        String desc;

        List<String> values = new ArrayList<>();

        public void addValue(String value) {
            values.add(value);
        }

        public void setDefault(String value) {
            if (StrUtil.isNotBlank(example)) {
                this.example = value;
            }
        }

        public boolean isValid() {
            return StrUtil.isNotBlank(desc) && !values.isEmpty();
        }
    }

    private static final class RequestParameterReplacer {
        private final Map<String, RequestParameter> destination;
        private final Map<String, RequestParameter> source;

        RequestParameterReplacer(
                Collection<RequestParameter> destination,
                Collection<RequestParameter> source) {
            this.destination = destination.stream()
                    .collect(toMap(RequestParameter::getName, Function.identity()));
            this.source = source.stream()
                    .collect(toMap(
                            RequestParameter::getName,
                            Function.identity()));
        }

        public Set<RequestParameter> replace() {
            return destination.values().stream()
                    .map(it -> source.getOrDefault(it.getName(), it))
                    .collect(Collectors.toSet());
        }
    }
}
