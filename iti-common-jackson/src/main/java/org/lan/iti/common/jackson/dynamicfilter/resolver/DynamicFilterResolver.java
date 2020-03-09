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

package org.lan.iti.common.jackson.dynamicfilter.resolver;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import org.springframework.core.MethodParameter;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.function.Function;

/**
 * {@link PropertyFilter} 处理器基类
 * 自定义注解:
 * 1. 定义注解
 * 2. 继承此类,覆写apply方法,使用SimpleBeanPropertyFilter中的方法来过滤 (@Component)
 * 3. 配置文件
 * # 快速失败,开启则装载 DynamicFilter 时报异常,否则仅 logger.error(xxx)
 * jackson.dynamic.filter.fail-fast=false
 * <p>
 * # 自定义resolver类路径,此resolver必须是spring-bean
 * jackson.dynamic.filter.resolver.class-names=this.is.my.Resolver,yet.another.Resolver
 *
 * @author NorthLan
 * @date 2020-03-05
 * @url https://noahlan.com
 */
public abstract class DynamicFilterResolver<A extends Annotation> extends TypeReference<A> implements Function<A, PropertyFilter> {

    @SuppressWarnings("unchecked")
    public PropertyFilter resolve(MethodParameter methodParameter) {
        return Optional.ofNullable(methodParameter.getMethodAnnotation((Class<A>) getType())).map(this).orElse(null);
    }
}
