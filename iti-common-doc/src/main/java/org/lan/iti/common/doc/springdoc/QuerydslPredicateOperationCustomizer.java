///*
// *
// *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
// *  *
// *  * Licensed under the Apache License, Version 2.0 (the "License");
// *  * you may not use this file except in compliance with the License.
// *  * You may obtain a copy of the License at
// *  *
// *  *     http://www.apache.org/licenses/LICENSE-2.0
// *  *
// *  * Unless required by applicable law or agreed to in writing, software
// *  * distributed under the License is distributed on an "AS IS" BASIS,
// *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  * See the License for the specific language governing permissions and
// *  * limitations under the License.
// *
// */
//
//package org.lan.iti.common.doc.springdoc;
//
//import com.querydsl.core.types.Path;
//import io.swagger.v3.core.converter.ModelConverters;
//import io.swagger.v3.core.converter.ResolvedSchema;
//import io.swagger.v3.core.util.PrimitiveType;
//import io.swagger.v3.oas.models.Operation;
//import io.swagger.v3.oas.models.media.Schema;
//import io.swagger.v3.oas.models.parameters.Parameter;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.commons.lang3.reflect.FieldUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.core.MethodParameter;
//import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
//import org.springframework.data.querydsl.binding.QuerydslBindings;
//import org.springframework.data.querydsl.binding.QuerydslBindingsFactory;
//import org.springframework.data.querydsl.binding.QuerydslPredicate;
//import org.springframework.data.util.CastUtils;
//import org.springframework.data.util.ClassTypeInformation;
//import org.springframework.data.util.TypeInformation;
//import org.springframework.web.method.HandlerMethod;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.Modifier;
//import java.lang.reflect.Type;
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * 修复由于在 SpringBoot 2.3.x 中 {@link QuerydslPredicate} 属性变更导致的npe
// * <pre>
// *     1. 兼容 SpringBoot 2.2.x 与 SpringBoot 2.3.x
// *     2. 利用到{@link QuerydslBindings#excludeUnlistedProperties} 特性,实际bind参数
// * </pre>
// * {@link org.springdoc.data.rest.customisers.QuerydslPredicateOperationCustomizer}
// *
// * @author NorthLan
// * @date 2020-09-15
// * @url https://noahlan.com
// * @see <a href="https://github.com/springdoc/springdoc-openapi/commit/24327770efec5fdac61c27fecdab34decc0d00fb#diff-af26767215a211e631e255aba431c7cf"></a>
// */
//@SuppressWarnings("unchecked")
//public class QuerydslPredicateOperationCustomizer extends org.springdoc.data.rest.customisers.QuerydslPredicateOperationCustomizer {
//
//    /**
//     * The constant LOGGER.
//     */
//    private static final Logger LOGGER = LoggerFactory.getLogger(org.springdoc.data.rest.customisers.QuerydslPredicateOperationCustomizer.class);
//
//    /**
//     * The Querydsl bindings factory.
//     */
//    private final QuerydslBindingsFactory querydslBindingsFactory;
//
//    /**
//     * Instantiates a new Querydsl predicate operation customizer.
//     *
//     * @param querydslBindingsFactory the querydsl bindings factory
//     */
//    public QuerydslPredicateOperationCustomizer(QuerydslBindingsFactory querydslBindingsFactory) {
//        super(querydslBindingsFactory);
//        this.querydslBindingsFactory = querydslBindingsFactory;
//    }
//
//    @Override
//    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
//        if (operation.getParameters() == null) {
//            return operation;
//        }
//
//        MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
//
//        List<Parameter> parametersToAddToOperation = new ArrayList<>();
//        for (MethodParameter parameter : methodParameters) {
//            QuerydslPredicate predicate = parameter.getParameterAnnotation(QuerydslPredicate.class);
//
//            if (predicate == null) {
//                continue;
//            }
//
//            QuerydslBindings bindings = extractQdslBindings(predicate);
//
//            Set<String> fieldsToAdd = Arrays.stream(predicate.root().getDeclaredFields())
//                    .filter(field -> !Modifier.isStatic(field.getModifiers()))
//                    .map(Field::getName).collect(Collectors.toSet());
//
//            Map<String, Object> pathSpecMap = getPathSpec(bindings, "pathSpecs");
//            //remove blacklisted fields
//            Set<String> blacklist = getFieldValues(bindings, "denyList", "blackList");
//            fieldsToAdd.removeIf(blacklist::contains);
//
//            Set<String> whiteList = getFieldValues(bindings, "allowList", "whiteList");
//            Set<String> aliases = getFieldValues(bindings, "aliases", null);
//
//            fieldsToAdd.addAll(aliases);
//            fieldsToAdd.addAll(whiteList);
//
//            boolean excludeUnlistedProperties = getFieldValueOfBoolean(bindings, "excludeUnlistedProperties", null);
//            for (String fieldName : fieldsToAdd) {
//                Type type = getFieldType(fieldName, pathSpecMap, predicate.root(), excludeUnlistedProperties);
//                if (type != null) {
//                    Parameter newParameter = buildParam(type, fieldName);
//
//                    parametersToAddToOperation.add(newParameter);
//                }
//            }
//        }
//        operation.getParameters().addAll(parametersToAddToOperation);
//        return operation;
//    }
//
//    /**
//     * Extract qdsl bindings querydsl bindings.
//     *
//     * @param predicate the predicate
//     * @return the querydsl bindings
//     */
//    private QuerydslBindings extractQdslBindings(QuerydslPredicate predicate) {
//        ClassTypeInformation<?> classTypeInformation = ClassTypeInformation.from(predicate.root());
//        TypeInformation<?> domainType = classTypeInformation.getRequiredActualType();
//
//        Optional<Class<? extends QuerydslBinderCustomizer<?>>> bindingsAnnotation = Optional.of(predicate)
//                .map(QuerydslPredicate::bindings)
//                .map(CastUtils::cast);
//
//        return bindingsAnnotation
//                .map(it -> querydslBindingsFactory.createBindingsFor(domainType, it))
//                .orElseGet(() -> querydslBindingsFactory.createBindingsFor(domainType));
//    }
//
//    /**
//     * Gets field values.
//     *
//     * @param instance  the instance
//     * @param fieldName the field name
//     * @return the field values
//     */
//    private Set<String> getFieldValues(QuerydslBindings instance, String fieldName, String alternativeFieldName) {
//        try {
//            Field field = FieldUtils.getDeclaredField(instance.getClass(), fieldName, true);
//            if (field == null && alternativeFieldName != null) {
//                field = FieldUtils.getDeclaredField(instance.getClass(), alternativeFieldName, true);
//            }
//            if (field != null) {
//                return (Set<String>) field.get(instance);
//            }
//        } catch (IllegalAccessException e) {
//            LOGGER.warn(e.getMessage());
//        }
//        return Collections.emptySet();
//    }
//
//    /**
//     * Gets field value of boolean.
//     *
//     * @param instance             the instance
//     * @param fieldName            the field name
//     * @param alternativeFieldName the alternative field name
//     * @return the field value of boolean.
//     */
//    private boolean getFieldValueOfBoolean(QuerydslBindings instance, String fieldName, String alternativeFieldName) {
//        try {
//            Field field = FieldUtils.getDeclaredField(instance.getClass(), fieldName, true);
//            if (field == null && alternativeFieldName != null) {
//                field = FieldUtils.getDeclaredField(instance.getClass(), alternativeFieldName, true);
//            }
//            if (field != null) {
//                return (boolean) field.get(instance);
//            }
//        } catch (IllegalAccessException e) {
//            LOGGER.warn(e.getMessage());
//        }
//        return false;
//    }
//
//    /**
//     * Gets path spec.
//     *
//     * @param instance  the instance
//     * @param fieldName the field name
//     * @return the path spec
//     */
//    private Map<String, Object> getPathSpec(QuerydslBindings instance, String fieldName) {
//        try {
//            Field field = FieldUtils.getDeclaredField(instance.getClass(), fieldName, true);
//            return (Map<String, Object>) field.get(instance);
//        } catch (IllegalAccessException e) {
//            LOGGER.warn(e.getMessage());
//        }
//        return Collections.emptyMap();
//    }
//
//    /**
//     * Gets path from path spec.
//     *
//     * @param instance the instance
//     * @return the path from path spec
//     */
//    private Optional<Path<?>> getPathFromPathSpec(Object instance) {
//        try {
//            if (instance == null) {
//                return Optional.empty();
//            }
//            Field field = FieldUtils.getDeclaredField(instance.getClass(), "path", true);
//            return (Optional<Path<?>>) field.get(instance);
//        } catch (IllegalAccessException e) {
//            LOGGER.warn(e.getMessage());
//        }
//        return Optional.empty();
//    }
//
//    /***
//     * Tries to figure out the Type of the field. It first checks the Qdsl pathSpecMap before checking the root class. Defaults to String.class
//     * @param fieldName The name of the field used as reference to get the type
//     * @param pathSpecMap The Qdsl path specifications as defined in the resolved bindings
//     * @param root The root type where the paths are gotten
//     * @return The type of the field. Returns
//     */
//    private Type getFieldType(String fieldName, Map<String, Object> pathSpecMap, Class<?> root, boolean excludeUnlistedProperties) {
//        try {
//            Object pathAndBinding = pathSpecMap.get(fieldName);
//            Optional<Path<?>> path = getPathFromPathSpec(pathAndBinding);
//
//            Type genericType;
//            Field declaredField = null;
//            if (path.isPresent()) {
//                genericType = path.get().getType();
//            } else if (!excludeUnlistedProperties) {
//                declaredField = root.getDeclaredField(fieldName);
//                genericType = declaredField.getGenericType();
//            } else {
//                return null;
//            }
//            if (genericType != null) {
//                return genericType;
//            }
//        } catch (NoSuchFieldException e) {
//            LOGGER.warn("Field {} not found on {} : {}", fieldName, root.getName(), e.getMessage());
//        }
//        return String.class;
//    }
//
//    /***
//     * Constructs the parameter
//     * @param type The type of the parameter
//     * @param name The name of the parameter
//     * @return The swagger parameter
//     */
//    private io.swagger.v3.oas.models.parameters.Parameter buildParam(Type type, String name) {
//        io.swagger.v3.oas.models.parameters.Parameter parameter = new io.swagger.v3.oas.models.parameters.Parameter();
//
//        if (StringUtils.isBlank(parameter.getName())) {
//            parameter.setName(name);
//        }
//
//        if (StringUtils.isBlank(parameter.getIn())) {
//            parameter.setIn("query");
//        }
//
//        if (parameter.getSchema() == null) {
//            Schema<?> schema = null;
//            PrimitiveType primitiveType = PrimitiveType.fromType(type);
//            if (primitiveType != null) {
//                schema = primitiveType.createProperty();
//            } else {
//                ResolvedSchema resolvedSchema = ModelConverters.getInstance()
//                        .resolveAsResolvedSchema(
//                                new io.swagger.v3.core.converter.AnnotatedType(type).resolveAsRef(true));
//                // could not resolve the schema or this schema references other schema
//                // we dont want this since there's no reference to the components in order to register a new schema if it doesnt already exist
//                // defaulting to string
//                if (resolvedSchema == null || !resolvedSchema.referencedSchemas.isEmpty()) {
//                    schema = PrimitiveType.fromType(String.class).createProperty();
//                } else {
//                    schema = resolvedSchema.schema;
//                }
//            }
//            parameter.setSchema(schema);
//        }
//        return parameter;
//    }
//}
