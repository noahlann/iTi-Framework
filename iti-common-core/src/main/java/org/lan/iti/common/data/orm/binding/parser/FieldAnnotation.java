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

package org.lan.iti.common.data.orm.binding.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.annotation.Annotation;

/**
 * 字段名与注解的包装对象关系
 *
 * @author NorthLan
 * @date 2020-04-18
 * @url https://noahlan.com
 */
@AllArgsConstructor
@Getter
public class FieldAnnotation {
    /**
     * 字段名
     */
    private String fieldName;
    /**
     * 字段类型
     */
    private Class<?> fieldClass;
    /**
     * 注解
     */
    private Annotation annotation;
}
