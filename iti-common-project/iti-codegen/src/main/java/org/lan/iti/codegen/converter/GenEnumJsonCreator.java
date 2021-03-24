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

package org.lan.iti.codegen.converter;

import java.lang.annotation.*;

/**
 * 生成Enum的@JsonCreator
 * <p>
 * {@code
 *
 * @author NorthLan
 * @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
 * public static Enum resolve(String code) {
 * for (Enum value : Enum.values()) {
 * if (StrUtil.equals(value.getCode(), code)) {
 * return value;
 * }
 * }
 * return Enum.DEFAULT;
 * }
 * }
 * @date 2021-03-24
 * @url https://noahlan.com
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Inherited
@Documented
public @interface GenEnumJsonCreator {
    /**
     * 预生成的类包名
     * 默认为当前位置
     */
    String pkgName() default "";

    /**
     * 待转换的值变量名
     * <p>变量类型将自动获取
     */
    String valueField() default "code";
}
