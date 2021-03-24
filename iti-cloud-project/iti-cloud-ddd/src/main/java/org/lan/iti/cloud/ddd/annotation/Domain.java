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

package org.lan.iti.cloud.ddd.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 业务域（领域聚合根）
 * <p>
 * 一个界限上下文(Bounded Context)可以由1个核心域和N个支撑域组成
 * 核心域是业务的入口与出口，负责驱动业务逻辑的执行
 * 支撑域为核心域提供必要的支撑，是某个功能模块的自治体系，要求支撑域必须能够自治
 *
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
public @interface Domain {
    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     *
     * @return the suggested component name, if any (or empty String otherwise)
     * @see Component#value()
     */
    @AliasFor(annotation = Component.class, value = "value")
    String value() default "";

    /**
     * 业务域编号
     */
    String code() default "";

    /**
     * 业务域名称
     */
    String name() default "";
}
