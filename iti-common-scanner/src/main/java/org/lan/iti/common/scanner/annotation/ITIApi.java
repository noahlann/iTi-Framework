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

package org.lan.iti.common.scanner.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 资源标识注解
 *
 * @author NorthLan
 * @date 2020-03-05
 * @url https://noahlan.com
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ITIApi {
    /**
     * 资源/模块 名称(必填项)
     * {@link ITIApi#name()}
     */
    @AliasFor("name")
    String value() default "";

    /**
     * 资源/模块 名称(必填项)
     * <p>
     * 位于方法上: 资源名
     * 位于Controller类上: 模块名
     */
    @AliasFor("value")
    String name() default "";

    /**
     * <pre>
     * 资源编码唯一标识.
     *
     * 说明:
     *      1.可不填写此注解属性.
     *      2.若不填写,则默认生成的编码标识为: 控制器类名称 + 分隔符 + 方法名称.
     *      3.若编码存在重复则系统启动异常
     */
    String code() default "";

    /**
     * 是否需要鉴权
     */
    boolean requiredPermission() default true;
}
