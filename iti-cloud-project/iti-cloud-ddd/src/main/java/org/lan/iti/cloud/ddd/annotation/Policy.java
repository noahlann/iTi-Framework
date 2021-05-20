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

import org.lan.iti.common.ddd.ext.IDomainExtension;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 扩展点定位策略，需要实现{@link org.lan.iti.common.ddd.ext.IExtPolicy}接口.
 *
 * <p>一个扩展点定位策略，只能对应一个扩展点类型：从该扩展点的多个实例中选取一个.</p>
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
@Deprecated
public @interface Policy {

    /**
     * 该扩展点定位策略对应的扩展点类型.
     *
     * @see Policy#extClazz
     */
    @AliasFor("extClazz")
    Class<? extends IDomainExtension> value();

    /**
     * 该扩展点定位策略对应的扩展点类型.
     */
    @AliasFor("value")
    Class<? extends IDomainExtension> extClazz();
}
