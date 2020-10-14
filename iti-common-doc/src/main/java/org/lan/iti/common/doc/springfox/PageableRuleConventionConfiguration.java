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

package org.lan.iti.common.doc.springfox;

import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.ApiParam;
import org.lan.iti.common.core.util.AnnotationProxy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
 * Swagger2 规则转换器
 * <pre>
 *     将Pageable转换为swagger2可读取规则,默认使用Pageable传递时,swagger直接读取其接口各种getter方法
 *     而spring-data将通过{@link org.springframework.data.web.PageableHandlerMethodArgumentResolver}来读取Pageable数据
 * </pre>
 *
 * @author NorthLan
 * @date 2020-09-13
 * @url https://noahlan.com
 */
@Configuration
@ConditionalOnClass({Pageable.class, SpringDataWebProperties.class})
public class PageableRuleConventionConfiguration {

    @Bean
    public AlternateTypeRuleConvention pageableConvention(final SpringDataWebProperties webProperties) {
        return new AlternateTypeRuleConvention() {
            @Override
            public List<AlternateTypeRule> rules() {
                return Collections.singletonList(
                        AlternateTypeRules.newRule(Pageable.class, pageableMixin(webProperties.getPageable(), webProperties.getSort())));
            }

            @Override
            public int getOrder() {
                return Ordered.LOWEST_PRECEDENCE;
            }
        };
    }


    private Type pageableMixin(SpringDataWebProperties.Pageable pageable, SpringDataWebProperties.Sort sort) {
        final String firstPage = pageable.isOneIndexedParameters() ? "1" : "0";
        return new AlternateTypeBuilder()
                .fullyQualifiedClassName(fullyQualifiedName())
                .property(property(pageable.getPrefix() + pageable.getPageParameter(),
                        Integer.class,
                        ImmutableMap.of(
                                "value", "页码, " + String.format("允许范围[%s, %s]", firstPage, Integer.MAX_VALUE),
                                "defaultValue", firstPage,
                                "allowableValues", String.format("range[%s, %s]", firstPage, Integer.MAX_VALUE),
                                "example", firstPage,
                                "required", false
                        )))
                .property(property(pageable.getPrefix() + pageable.getSizeParameter(),
                        Integer.class,
                        ImmutableMap.of(
                                "value", "每页大小, " + String.format("允许范围[1, %s]", pageable.getMaxPageSize()),
                                "defaultValue", String.valueOf(pageable.getDefaultPageSize()),
                                "allowableValues", String.format("range[1, %s]", pageable.getMaxPageSize()),
                                "example", "20",
                                "required", false)))
                .property(property(sort.getSortParameter(),
                        String[].class,
                        ImmutableMap.of(
                                "value", "页面排序, 格式: 字段名,(asc|desc). asc表示升序,desc表示降序"
                        )))
                .build();
    }

    private String fullyQualifiedName() {
        return String.format("%s.generated.%s", Pageable.class.getPackage().getName(), Pageable.class.getSimpleName());
    }

    private AlternateTypePropertyBuilder property(String name, Class<?> type, Map<String, Object> parameters) {
        return new AlternateTypePropertyBuilder()
                .withName(name)
                .withType(type)
                .withCanRead(true)
                .withCanWrite(true)
                .withAnnotations(Collections.singletonList(AnnotationProxy.of(ApiParam.class, parameters)));
    }
}
