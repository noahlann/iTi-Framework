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
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * 领域活动步骤
 * 需注解在 {@link org.lan.iti.common.ddd.step.IDomainStep}之上
 *
 * @author NorthLan
 * @date 2021-02-07
 * @url https://noahlan.com
 * @deprecated 不再使用step编排，意义不大
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Service
@Deprecated
public @interface Step {

    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     *
     * @return the suggested component name, if any
     */
    @AliasFor(annotation = Service.class, attribute = "value")
    String value() default "";

    /**
     * 该步骤的名称.
     */
    String name() default "";

    /**
     * 该步骤所属分组.
     * <p>
     * <p>有的步骤非常大，例如：订单商品校验，涉及非常多的逻辑</p>
     * <p>这时候可以把它拆成多个步骤，但统一到“商品校验”分组里</p>
     * <p>分组，可以理解为标签：tag</p>
     */
    String[] tags() default {};

//    /**
//     * 该步骤依赖哪些其他步骤.
//     * <p>
//     * <p>即，被依赖的步骤先执行，才能执行本步骤</p>
//     */
//    Class<? extends IDomainStep>[] dependsOn() default {};
}
