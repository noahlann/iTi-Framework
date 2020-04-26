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

package org.lan.iti.common.jackson.dynamicfilter.annotation;


import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 过滤配置 注解
 * <p>
 * 用于方法上的注解，可重复使用
 * </p>
 * <p>
 * 如：过滤用户两个字段
 *
 * @author NorthLan
 * @code @ITIFilter(excludes = {"password", "avatar"}, type = User.class)
 * @code public ApiResult<User> info() {...}
 * @date 2020-03-05
 * @url https://noahlan.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ITIFilters.class)
public @interface ITIFilter {

    /**
     * 过滤指定类
     * 默认: {@link Object} 过滤所有类,可不指定
     */
    @AliasFor("type")
    Class<?> value() default Object.class;

    /**
     * 仅排除字段列表
     * 与 {@link ITIFilter#includes()} 冲突,默认选取 excludes
     */
    String[] excludes() default {};

    /**
     * 仅包含字段列表
     * 与 {@link ITIFilter#excludes()} 冲突,默认选取 {@link ITIFilter#excludes()}
     */
    String[] includes() default {};

    /**
     * 过滤指定类
     * 默认: {@link Object} 过滤所有类,可不指定
     */
    @AliasFor("value")
    Class<?> type() default Object.class;
}
