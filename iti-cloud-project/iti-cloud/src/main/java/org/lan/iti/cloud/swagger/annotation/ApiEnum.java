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

package org.lan.iti.cloud.swagger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * SwaggerAnnotation 扩展 ApiEnum
 * <p>使用方式</p>
 * <code>
 * \@ApiEnum(value = "id", desc = "desc")
 * public enum Test {
 * TEST(1, "测试"),
 * RESULT(2, "结果"),
 * ;
 * \@Getter
 * private final int id;
 * <p>
 * \@Getter
 * private final String desc;
 * }
 * ->
 * 将生成 markdown 形式说明
 * Test:
 * * 1:测试
 * * 2:结果
 * </code>
 *
 * @author NorthLan
 * @date 2021-03-22
 * @url https://noahlan.com
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiEnum {

    /**
     * 取描述
     * <p>仅作用于 局部变量 时生效</p>
     */
    String desc() default "";

    /**
     * 取值
     * <p>仅作用于类时生效, 取valueField指定字段取值</p>
     */
    String valueField() default "";

    /**
     * 取描述
     * <p>仅作用于类时生效, 取descField指定的字段值</p>
     */
    String descField() default "";
}
