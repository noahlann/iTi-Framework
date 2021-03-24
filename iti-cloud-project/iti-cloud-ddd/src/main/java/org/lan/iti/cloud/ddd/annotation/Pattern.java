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

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 业务模式身份，需要实现{@link org.lan.iti.common.ddd.ext.IIdentityResolver}接口.
 * <p>
 * <p>{@link Pattern}通常属于中台CP自己要处理的个性化业务，前台BP往往不会参与</p>
 * <p>横向视角把扩展点进行聚合，属于水平业务，可以叠加</p>
 * <p>所谓横向视角，相当于AOP里的Aspect</p>
 * <ul>如何理解{@code Pattern}与{@code IDomainExtension}？
 * <li>{@code Pattern}，相当于把散落在各处的某个业务逻辑{@code if} 判断条件，收敛到{@code Pattern}里，使得这些业务判断显式化，有形化，有了化身，并有了个名字(UL)</li>
 * <li>扩展点，相当于把{@code if} 后面的code block显式化，有形化</li>
 * </ul>
 *
 * @author NorthLan
 * @date 2021-02-24
 * @url https://noahlan.com
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
public @interface Pattern {
    /**
     * 业务模式编号.
     */
    String code() default "";

    /**
     * 业务模式名称.
     */
    String name() default "";

    /**
     * 该业务模式的业务标签.
     */
    String[] tags() default {};

    /**
     * 优先级，越小表示优先级越高.
     * <p>
     * <p>用于解决业务模式匹配的顺序问题</p>
     * <p>只应用于同一个扩展点在不同{@code Pattern}间的顺序问题，不同的扩展点间优先级不具备可比性</p>
     */
    int priority() default 1024;
}
