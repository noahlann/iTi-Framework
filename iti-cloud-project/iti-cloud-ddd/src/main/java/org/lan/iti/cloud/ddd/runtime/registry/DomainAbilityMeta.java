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
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.ddd.annotation.DomainAbility;
import org.lan.iti.cloud.ddd.runtime.AbstractDomainAbility;
import org.lan.iti.common.core.exception.BootstrapException;
import org.lan.iti.cloud.util.AopUtils;
import org.lan.iti.common.ddd.ext.IDomainExtension;
import org.springframework.core.ResolvableType;

import javax.validation.constraints.NotNull;

/**
 * @author NorthLan
 * @date 2021-02-22
 * @url https://noahlan.com
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@ToString
@Slf4j
@Getter
@Deprecated
class DomainAbilityMeta implements IRegistryAware {
    private String domain;
    private String name;
    private AbstractDomainAbility domainAbilityBean;
    private Class<? extends AbstractDomainAbility> domainAbilityClass;
    private Class<? extends IDomainExtension> extClass;

    @Override
    public void registerBean(@NotNull Object bean) {
        DomainAbility domainAbility = AopUtils.getAnnotation(bean, DomainAbility.class);
        this.domain = domainAbility.domain();
        this.name = domainAbility.name();
        if (!(bean instanceof AbstractDomainAbility)) {
            throw BootstrapException.ofMessage(bean.getClass().getCanonicalName(), " MUST extend BaseDomainAbility");
        }

        // bean
        this.domainAbilityBean = (AbstractDomainAbility) bean;
        this.domainAbilityClass = (Class<? extends AbstractDomainAbility>) AopUtils.getTarget(bean).getClass();
        this.resolveExtClazz();

        log.debug("domain ability: {} ext: {}", bean.getClass().getCanonicalName(), extClass.getCanonicalName());
        InternalIndexer.index(this);
    }

    private void resolveExtClazz() {
        ResolvableType baseDomainAbilityType = ResolvableType.forClass(this.domainAbilityClass).getSuperType();
        // 5 inheritance? much enough
        final int retryTimes = 5;
        for (int i = 0; i < retryTimes; i++) {
            for (ResolvableType resolvableType : baseDomainAbilityType.getGenerics()) {
                Class<?> resolvableTypeClazz = resolvableType.resolve();
                if (resolvableTypeClazz == null) {
                    continue;
                }
                if (IDomainExtension.class.isAssignableFrom(resolvableTypeClazz)) {
                    this.extClass = (Class<? extends IDomainExtension>) resolvableType.resolve();
                    return;
                }
            }
            // parent class
            baseDomainAbilityType = baseDomainAbilityType.getSuperType();
        }
        // should never happen: otherwise java cannot compile
        throw BootstrapException.ofMessage("Even after 5 tries, still unable to figure out the extension class of BaseDomainAbility:", this.domainAbilityClass.getCanonicalName());
    }
}
