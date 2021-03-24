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

package org.lan.iti.cloud.codegen.repository.jpa;

import org.lan.iti.common.ddd.IBaseDDDTranslator;

import java.lang.annotation.*;

/**
 * 生成 JPA 仓储基础接口
 *
 * @author NorthLan
 * @date 2021-02-06
 * @url https://noahlan.com
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Inherited
@Documented
public @interface GenJpaRepository {
    /**
     * 目标包名,不填默认当前类的包名
     */
    String pkgName() default "";

    /**
     * 类名,不填默认为 BaseXXXRepository
     */
    String clsName() default "";

    /**
     * po类型
     */
    Class<?> poClazz();

    /**
     * 转换器class
     */
    Class<? extends IBaseDDDTranslator> translatorClazz();

    /**
     * BaseJpaRepository 中的变量 translator 是否使用MapStruct的Mappers.getMapper方法获取
     */
    boolean useMapFactory() default true;
}
