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

package org.lan.iti.cloud.api;

import cn.hutool.core.collection.CollUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.lan.iti.cloud.constants.AopConstants;
import org.lan.iti.common.core.api.ApiResult;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;

/**
 * 返回值包装为统一格式
 * <p>
 * {@code ResponseBody} / {@code ResponseEntity} 在写入 {@code HttpMessageConverter} 之前执行
 * </p>
 *
 * @author NorthLan
 * @date 2021-07-22
 * @url https://noahlan.com
 */
@RestControllerAdvice
@AllArgsConstructor
@Order(AopConstants.API_RESULT_WRAPPER_ADVICE)
public class ApiResultWrapperAdvice implements ResponseBodyAdvice<Object> {
    private final ObjectMapper objectMapper;
    private final ApiResultWrapperProperties properties;
    private final SpringDataWebProperties webProperties;
    // matcher
    private final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public boolean supports(@NonNull MethodParameter returnType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return filter(returnType);
    }

    @SuppressWarnings("rawtypes")
    @SneakyThrows
    @Override
    public Object beforeBodyWrite(Object body,
                                  @NonNull MethodParameter returnType,
                                  @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request,
                                  @NonNull ServerHttpResponse response) {
        // URL匹配过滤
        if (filterUrl(request)) {
            return body;
        }
        if (body == null) {
            // 返回值为空，默认返回success
            // 当类型为String时将发生 cast异常，处理
            if (returnType.getParameterType().equals(String.class)) {
                return objectMapper.writeValueAsString(ApiResult.ok());
            }
            return ApiResult.ok();
        }
        // 返回String需要特殊处理
        if (body instanceof String) {
            return objectMapper.writeValueAsString(ApiResult.ok(body));
        }
        ApiResult result = null;
        // 返回ApiResult不再进行封装
        if (body instanceof ApiResult) {
            // 根据body指定code转换httpStatus
            result = (ApiResult) body;
            HttpStatus status = HttpStatus.resolve(result.getCodeInt());
            if (status != null) {
                // 设置为HTTP标准返回码
                response.setStatusCode(status);
            }
        }

        // 封装body
        if (result == null) {
            result = ApiResult.ok(body);
        }
        // 增强page(jpa)
        withPage(result, result.getData());
        return result;
    }

    /**
     * 处理Page相关内容
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void withPage(ApiResult result, Object body) {
        if (body instanceof Page) {
            Page page = (Page) body;

            result.data(page.getContent())
                    .totalPages(page.getTotalPages())
                    .totalElements(page.getTotalElements())
                    .size(page.getSize())
                    .page(webProperties.getPageable().isOneIndexedParameters() ? page.getNumber() + 1 : page.getNumber());
        }
    }

    private boolean filterUrl(ServerHttpRequest request) {
        return properties.getExcludePath().stream().anyMatch(it -> matcher.match(it, request.getURI().getPath()));
    }

    private boolean filter(@NonNull MethodParameter returnType) {
        // 过滤
        Class<?> declaringClass = returnType.getDeclaringClass();
        Method method = returnType.getMethod();
        if (method == null) {
            return false;
        }
        // 配置自定义包过滤
        if (CollUtil.isNotEmpty(properties.getIncludePackages())) {
            if (properties.getIncludePackages().stream().noneMatch(it -> matcher.match(it, declaringClass.getPackage().getName()))) {
                return false;
            }
        }

        if (properties.getExcludePackages().stream()
                .anyMatch(it -> matcher.match(it, declaringClass.getPackage().getName()))) {
            return false;
        }
        // 配置式自定义类过滤
        if (properties.getExcludeClasses().contains(declaringClass.getName())) {
            return false;
        }
        if (method.isAnnotationPresent(IgnoreResponseBodyWrapper.class)) {
            return false;
        }
        //noinspection RedundantIfStatement
        if (declaringClass.isAnnotationPresent(IgnoreResponseBodyWrapper.class)) {
            return false;
        }
        return true;
    }
}
