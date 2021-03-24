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

package org.lan.iti.cloud.codegen.converter;

import java.lang.annotation.*;

/**
 * 生成JPA枚举转化器
 *
 * @author NorthLan
 * @date 2021-02-01
 * @url https://noahlan.com
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Inherited
@Documented
public @interface GenJpaEnumConverter {
    /**
     * 预生成的类包名
     * 默认为当前位置
     */
    String pkgName() default "";

    /**
     * 指定转换的field名称
     * <p>
     * {@code
     * for (SexEnum value : SexEnum.values()){
     *      if (value.getCode() == i){
     *          return value;
     *       }
     * }
     * }
     * <p>其中getCode的code,若不指定则取第一个局部变量
     */
    String value();
}
