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

package org.lan.iti.cloud.ddd.runtime.registry;

import lombok.Getter;
import lombok.ToString;
import org.lan.iti.cloud.util.AopUtils;
import org.lan.iti.cloud.ddd.annotation.Domain;

import javax.validation.constraints.NotNull;

/**
 * {@link Domain}注解 元数据
 *
 * @author NorthLan
 * @date 2021-02-08
 * @url https://noahlan.com
 */
@Getter
@ToString
@Deprecated
class DomainMeta implements IRegistryAware {
    private String code;
    private String name;
    private Object objectBean;

    @Override
    public void registerBean(@NotNull Object bean) {
        Domain domain = AopUtils.getAnnotation(bean, Domain.class);
        this.code = domain.code();
        this.name = domain.name();
        this.objectBean = bean;

        InternalIndexer.index(this);
    }
}
