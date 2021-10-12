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

package org.lan.iti.cloud.jackson.dynamicfilter.support;

import com.fasterxml.jackson.databind.ser.PropertyFilter;
import org.lan.iti.cloud.constants.AopConstants;
import org.lan.iti.cloud.jackson.dynamicfilter.resolver.DynamicFilterResolver;
import org.lan.iti.cloud.jackson.dynamicfilter.resolver.ITIFilterResolver;
import org.lan.iti.cloud.jackson.dynamicfilter.resolver.ITIFiltersResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMappingJacksonResponseBodyAdvice;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Controller aop 切面拦截
 * 运行时根据 {@link DynamicFilterResolver} 动态切换 {@link PropertyFilter}
 *
 * @author NorthLan
 * @date 2020-03-05
 * @url https://noahlan.com
 */
@ControllerAdvice
@Order(AopConstants.DYNAMIC_FILTER_ADVICE)
public class DynamicFilterResponseBodyAdvice extends AbstractMappingJacksonResponseBodyAdvice {
    private final Map<Class<Annotation>, DynamicFilterResolver<?>> resolvers = new HashMap<>();

    public DynamicFilterResponseBodyAdvice() {
        addResolvers(new ITIFiltersResolver(), new ITIFilterResolver());
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return super.supports(returnType, converterType) && resolvers.keySet().stream().anyMatch(returnType::hasMethodAnnotation);
    }

    @Override
    protected void beforeBodyWriteInternal(MappingJacksonValue mappingJacksonValue, MediaType mediaType, MethodParameter methodParameter, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        resolveFilter(methodParameter).map(DynamicFilterProvider::new).ifPresent(mappingJacksonValue::setFilters);
    }

    private Optional<PropertyFilter> resolveFilter(MethodParameter methodParameter) {
        return resolvers.values().stream().map(it -> it.resolve(methodParameter)).filter(Objects::nonNull).findFirst();
    }

    /**
     * Add resolvers.
     * NOTE: Resolvers will be distinct by {@link DynamicFilterResolver#getType()}
     *
     * @param resolver DynamicFilterResolver
     * @param more     DynamicFilterResolver
     */
    @SuppressWarnings("unchecked")
    public void addResolvers(DynamicFilterResolver<?> resolver, DynamicFilterResolver<?>... more) {
        resolvers.put((Class<Annotation>) resolver.getType(), resolver);
        Arrays.stream(more).filter(Objects::nonNull).forEach(it -> resolvers.put((Class<Annotation>) it.getType(), it));
    }
}
