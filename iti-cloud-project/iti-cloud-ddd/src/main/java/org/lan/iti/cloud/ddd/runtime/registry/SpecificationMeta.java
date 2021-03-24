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
import org.lan.iti.cloud.ddd.annotation.Specification;
import org.lan.iti.cloud.util.AopUtils;
import org.lan.iti.common.core.exception.BootstrapException;
import org.lan.iti.common.ddd.specification.ISpecification;

import javax.validation.constraints.NotNull;
import java.lang.reflect.ParameterizedType;

/**
 * Specification注解 元数据
 *
 * @author NorthLan
 * @date 2021-02-22
 * @url https://noahlan.com
 */
@Getter
@ToString
public class SpecificationMeta implements IRegistryAware {
    private String name;
    private String[] tags;
    private ISpecification<?> specificationBean;
    private Class<?> domainClazz;

    @Override
    public void registerBean(@NotNull Object bean) {
        Specification specification = AopUtils.getAnnotation(bean, Specification.class);
        this.name = specification.value();
        this.tags = specification.tags();
        if (!(bean instanceof ISpecification)) {
            throw BootstrapException.ofMessage(bean.getClass().getCanonicalName(), " MUST implement ISpecification");
        }
        this.specificationBean = (ISpecification<?>) bean;
        // domain class
        try {
            domainClazz = (Class<?>) ((ParameterizedType) this.specificationBean.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        } catch (Exception ignore) {
        }

        InternalIndexer.index(this);
    }
}
