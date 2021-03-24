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
import org.lan.iti.common.core.exception.BootstrapException;
import org.lan.iti.cloud.util.AopUtils;
import org.lan.iti.cloud.ddd.annotation.Pattern;
import org.lan.iti.common.ddd.ext.IDomainExtension;
import org.lan.iti.common.ddd.ext.IIdentityResolver;
import org.lan.iti.common.ddd.model.IDomain;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * {@link org.lan.iti.cloud.ddd.annotation.Pattern}注解 元数据
 *
 * @author NorthLan
 * @date 2021-02-24
 * @url https://noahlan.com
 */
@SuppressWarnings("rawtypes")
@Getter
@ToString
class PatternMeta implements IRegistryAware, IIdentityResolver {
    private String code;
    private String name;
    private int priority;
    private IIdentityResolver patternBean;
    private final Map<Class<? extends IDomainExtension>, ExtensionMeta> extensionMetaMap = new HashMap<>();

    @SuppressWarnings("unchecked")
    @Override
    public boolean match(IDomain iDomain) {
        return patternBean.match(iDomain);
    }

    @Override
    public void registerBean(@NotNull Object bean) {
        initialize(bean);

        InternalIndexer.index(this);
    }

    private void initialize(Object bean) {
        Pattern pattern = AopUtils.getAnnotation(bean, Pattern.class);
        this.code = pattern.code();
        this.name = pattern.name();
        this.priority = pattern.priority();
        if (this.priority < 0) {
            throw BootstrapException.ofMessage("Pattern.priority must be zero or positive");
        }
        if (!(bean instanceof IIdentityResolver)) {
            throw BootstrapException.ofMessage(bean.getClass().getCanonicalName(), " MUST implements IIdentityResolver");
        }
        this.patternBean = (IIdentityResolver) bean;
    }

    void registerExtensionMeta(ExtensionMeta extensionMeta) {
        Class<? extends IDomainExtension> extClazz = extensionMeta.getExtClazz();
        if (extensionMetaMap.containsKey(extClazz)) {
            throw BootstrapException.ofMessage("Pattern(code=", code, ") can hold ONLY one instance on ", extClazz.getCanonicalName(),
                    ", existing ", extensionMetaMap.get(extClazz).toString(), ", illegal ", extensionMeta.toString());
        }
        extensionMetaMap.put(extClazz, extensionMeta);
    }

    ExtensionMeta getExtension(Class<? extends IDomainExtension> extClazz) {
        return extensionMetaMap.get(extClazz);
    }

    Set<Class<? extends IDomainExtension>> extClazzSet() {
        return extensionMetaMap.keySet();
    }


}
