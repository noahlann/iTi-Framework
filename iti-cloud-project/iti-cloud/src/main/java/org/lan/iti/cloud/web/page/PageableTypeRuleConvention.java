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

package org.lan.iti.cloud.web.page;

import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.lan.iti.common.core.util.AnnotationProxy;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.core.Ordered;
import org.springframework.data.domain.Pageable;
import springfox.documentation.builders.AlternateTypeBuilder;
import springfox.documentation.builders.AlternateTypePropertyBuilder;
import springfox.documentation.schema.AlternateTypeRule;
import springfox.documentation.schema.AlternateTypeRuleConvention;
import springfox.documentation.schema.AlternateTypeRules;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Springfox 规则转换器
 * <p>
 * 将Pageable转换为springfox可读取规则,默认使用Pageable传递时,springfox直接读取其接口各种getter方法
 * 而spring-data将通过{@link org.springframework.data.web.PageableHandlerMethodArgumentResolver}来读取Pageable数据
 * </p>
 * <p>
 * 本解析器可修复springfox页面对于pageable的识别错误
 * </p>
 *
 * @author NorthLan
 * @date 2021-07-23
 * @url https://noahlan.com
 */
@AllArgsConstructor
public class PageableTypeRuleConvention implements AlternateTypeRuleConvention {
    private final SpringDataWebProperties properties;
    private final TypeResolver typeResolver;

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public List<AlternateTypeRule> rules() {
        return Collections.singletonList(
                AlternateTypeRules.newRule(typeResolver.resolve(Pageable.class), typeResolver.resolve(pageableMixin()))
        );
    }

    private Type pageableMixin() {
        SpringDataWebProperties.Pageable pageableProps = properties.getPageable();
        SpringDataWebProperties.Sort sortProps = properties.getSort();
        final String firstPage = pageableProps.isOneIndexedParameters() ? "1" : "0";
        return new AlternateTypeBuilder()
                .fullyQualifiedClassName(
                        String.format("%s.generated.%s",
                                Pageable.class.getPackage().getName(),
                                Pageable.class.getSimpleName()))
                // page
                .property(it -> setProperties(it, pageableProps.getPrefix() + pageableProps.getPageParameter(),
                        Integer.class,
                        ImmutableMap.of(
                                "value", "页码, " + String.format("允许范围[%s, %s]", firstPage, Integer.MAX_VALUE),
                                "defaultValue", firstPage,
                                "allowableValues", String.format("range[%s, %s]", firstPage, Integer.MAX_VALUE),
                                "example", firstPage,
                                "required", false
                        )))
                // size
                .property(it -> setProperties(it, pageableProps.getPrefix() + pageableProps.getSizeParameter(),
                        Integer.class,
                        ImmutableMap.of(
                                "value", "每页大小, " + String.format("允许范围[1, %s]", pageableProps.getMaxPageSize()),
                                "defaultValue", String.valueOf(pageableProps.getDefaultPageSize()),
                                "allowableValues", String.format("range[1, %s]", pageableProps.getMaxPageSize()),
                                "example", String.valueOf(pageableProps.getDefaultPageSize()),
                                "required", false)))
                // sort
                .property(it -> setProperties(it, sortProps.getSortParameter(),
                        String[].class,
                        ImmutableMap.of(
                                "value", "页面排序, 格式: 字段名(,asc|desc). asc表示升序,desc表示降序,不填默认为asc"
                        )))
                .build();
    }

    private void setProperties(AlternateTypePropertyBuilder builder, String name, Class<?> type, Map<String, Object> parameters) {
        builder
                .name(name)
                .type(type)
                .canRead(true)
                .canWrite(true)
                .annotations(Collections.singletonList(AnnotationProxy.of(ApiParam.class, parameters)));
    }
}
