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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import org.lan.iti.cloud.ddd.annotation.Policy;
import org.lan.iti.cloud.util.AopUtils;
import org.lan.iti.common.core.exception.BootstrapException;
import org.lan.iti.common.ddd.ext.IDomainExtension;
import org.lan.iti.common.ddd.ext.IExtPolicy;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link org.lan.iti.cloud.ddd.annotation.Policy}注解 元数据
 *
 * @author NorthLan
 * @date 2021-02-24
 * @url https://noahlan.com
 */
@SuppressWarnings("rawtypes")
@ToString
@Getter(AccessLevel.PACKAGE)
@Deprecated
class PolicyMeta implements IRegistryAware {

    private IExtPolicy policyBean;

    private Class<? extends IDomainExtension> extClazz;

    /**
     * 该扩展点策略控制的所有扩展点实例，key is extension.code
     */
    private final Map<String, ExtensionMeta> extensionMetaMap = new HashMap<>();

    @Override
    public void registerBean(@NotNull Object bean) {
        initialize(bean);

        InternalIndexer.index(this);
    }

    private void initialize(Object bean) {
        Policy policy = AopUtils.getAnnotation(bean, Policy.class);
        this.extClazz = policy.extClazz();
        if (!(bean instanceof IExtPolicy)) {
            throw BootstrapException.ofMessage(bean.getClass().getCanonicalName(), " MUST implements IExtPolicy");
        }
        this.policyBean = (IExtPolicy) bean;
    }

    void registerExtensionMeta(ExtensionMeta extensionDef) {
        extensionMetaMap.put(extensionDef.getCode(), extensionDef);
    }

    @NotNull
    ExtensionMeta getExtension(Object params) {
        // 根据领域模型，让扩展点定位策略计算目标扩展点code: will never be null
        final String extensionCode = policyBean.extensionCode(params);
        if (extensionCode == null) {
            return null;
        }

        return extensionMetaMap.get(extensionCode);
    }

    String policyName() {
        return policyBean.getClass().getCanonicalName();
    }
}
