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
 * 领域能力
 * 需注解在 {@link org.lan.iti.cloud.ddd.runtime.AbstractDomainAbility}之上
 *
 * @author NorthLan
 * @date 2021-02-07
 * @url https://noahlan.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
@Deprecated
public @interface DomainAbility {
    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     *
     * @see Component#value()
     */
    @AliasFor(annotation = Component.class, attribute = "value")
    String value() default "";

    /**
     * 所属业务域
     *
     * @see Domain#code()
     */
    String domain() default "";

    /**
     * 能力名称
     */
    String name() default "";

    /**
     * 该领域能力的业务标签
     * <p>
     *     通过标签，把众多的扩展点结构化管理
     * </p>
     */
    String[] tags() default {};
}
