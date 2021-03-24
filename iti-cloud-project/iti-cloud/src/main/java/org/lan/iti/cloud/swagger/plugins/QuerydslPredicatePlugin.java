/*
 *
 *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
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

import com.querydsl.core.types.Path;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.annotation.Order;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.util.CastUtils;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.NonNull;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.RequestParameter;
import springfox.documentation.service.ResolvedMethodParameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.OperationBuilderPlugin;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.swagger.common.SwaggerPluginSupport;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static springfox.documentation.builders.BuilderDefaults.nullToEmptyList;

/**
 * Querydsl方言插件
 * <p>
 * 支持querydsl的Predicate
 *
 * @author NorthLan
 * @date 2020-09-20
 * @url https://noahlan.com
 */
@Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
@Slf4j
@AllArgsConstructor
public class QuerydslPredicatePlugin implements OperationBuilderPlugin {
    private final QuerydslBindingsFactory querydslBindingsFactory;

    @Override
    public boolean supports(@NonNull DocumentationType documentationType) {
        return SwaggerPluginSupport.pluginDoesApply(documentationType);
    }

    @Override
    public void apply(OperationContext context) {
        List<ResolvedMethodParameter> methodParameters = context.getParameters();

        List<RequestParameter> parameters = new ArrayList<>();

        for (int i = 0; i < methodParameters.size(); i++) {
            ResolvedMethodParameter parameter = methodParameters.get(i);
            QuerydslPredicate predicate = parameter.findAnnotation(QuerydslPredicate.class).orElse(null);
            if (predicate == null) {
                continue;
            }

            QuerydslBindings bindings = extractQdslBindings(predicate);

            Set<String> fieldsToAdd = Arrays.stream(predicate.root().getDeclaredFields())
                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
                    .map(Field::getName)
                    .collect(Collectors.toSet());

            Map<String, Object> pathSpecMap = getPathSpec(bindings);
            // remove blacklisted fields
            Set<String> blacklist = getFieldValues(bindings, "denyList", "blackList");
            fieldsToAdd.removeIf(blacklist::contains);

            Set<String> whiteList = getFieldValues(bindings, "allowList", "whiteList");
            Set<String> aliases = getFieldValues(bindings, "aliases", null);

            fieldsToAdd.addAll(aliases);
            fieldsToAdd.addAll(whiteList);

            for (String fieldName : fieldsToAdd) {
//                Type type = getFieldType(fieldName, pathSpecMap, predicate.root());
                // TODO 这样支持还不够完整,有时间再深入研究,主要是RequestParameterBuilder#query 方法进行build
                parameters.add(new RequestParameterBuilder()
                        .accepts(nullToEmptyList(context.consumes()))
                        .parameterIndex(i)
                        .description(fieldName)
                        .required(false)
                        .name(fieldName)
                        .in(ParameterType.QUERY)
                        .build());
            }
        }
        context.operationBuilder().requestParameters(parameters);
    }

    /**
     * Extract qdsl bindings querydsl bindings.
     *
     * @param predicate the predicate
     * @return the querydsl bindings
     */
    private QuerydslBindings extractQdslBindings(QuerydslPredicate predicate) {
        ClassTypeInformation<?> classTypeInformation = ClassTypeInformation.from(predicate.root());
        TypeInformation<?> domainType = classTypeInformation.getRequiredActualType();

        Optional<Class<? extends QuerydslBinderCustomizer<?>>> bindingsAnnotation = Optional.of(predicate)
                .map(QuerydslPredicate::bindings)
                .map(CastUtils::cast);

        return bindingsAnnotation
                .map(it -> querydslBindingsFactory.createBindingsFor(domainType, it))
                .orElseGet(() -> querydslBindingsFactory.createBindingsFor(domainType));
    }

    /**
     * Gets path spec.
     *
     * @param instance the instance
     * @return the path spec
     */
    private Map<String, Object> getPathSpec(QuerydslBindings instance) {
        try {
            Field field = FieldUtils.getDeclaredField(instance.getClass(), "pathSpecs", true);
            return (Map<String, Object>) field.get(instance);
        } catch (IllegalAccessException e) {
            log.warn(e.getMessage());
        }
        return Collections.emptyMap();
    }

    /**
     * Gets path from path spec.
     *
     * @param instance the instance
     * @return the path from path spec
     */
    private Optional<Path<?>> getPathFromPathSpec(Object instance) {
        try {
            if (instance == null) {
                return Optional.empty();
            }
            Field field = FieldUtils.getDeclaredField(instance.getClass(), "path", true);
            return (Optional<Path<?>>) field.get(instance);
        } catch (IllegalAccessException e) {
            log.warn(e.getMessage());
        }
        return Optional.empty();
    }

    /***
     * Tries to figure out the Type of the field. It first checks the Qdsl pathSpecMap before checking the root class. Defaults to String.class
     * @param fieldName The name of the field used as reference to get the type
     * @param pathSpecMap The Qdsl path specifications as defined in the resolved bindings
     * @param root The root type where the paths are gotten
     * @return The type of the field. Returns
     */
    private Type getFieldType(String fieldName, Map<String, Object> pathSpecMap, Class<?> root) {
        try {
            Object pathAndBinding = pathSpecMap.get(fieldName);
            Optional<Path<?>> path = getPathFromPathSpec(pathAndBinding);

            Type genericType;
            Field declaredField = null;
            if (path.isPresent()) {
                genericType = path.get().getType();
            } else {
                declaredField = root.getDeclaredField(fieldName);
                genericType = declaredField.getGenericType();
            }
            if (genericType != null) {
                return genericType;
            }
        } catch (NoSuchFieldException e) {
            log.warn("Field {} not found on {} : {}", fieldName, root.getName(), e.getMessage());
        }
        return String.class;
    }

    /**
     * Gets field values.
     *
     * @param instance  the instance
     * @param fieldName the field name
     * @return the field values
     */
    private Set<String> getFieldValues(QuerydslBindings instance, String fieldName, String alternativeFieldName) {
        try {
            Field field = FieldUtils.getDeclaredField(instance.getClass(), fieldName, true);
            if (field == null && alternativeFieldName != null) {
                field = FieldUtils.getDeclaredField(instance.getClass(), alternativeFieldName, true);
            }
            if (field != null) {
                return (Set<String>) field.get(instance);
            }
        } catch (IllegalAccessException e) {
            log.warn(e.getMessage());
        }
        return Collections.emptySet();
    }
}
