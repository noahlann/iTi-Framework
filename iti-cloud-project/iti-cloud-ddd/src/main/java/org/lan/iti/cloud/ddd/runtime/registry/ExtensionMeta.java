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
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.exception.BootstrapException;
import org.lan.iti.cloud.util.AopUtils;
import org.lan.iti.cloud.ddd.annotation.Extension;
import org.lan.iti.common.ddd.ext.IDomainExtension;

import javax.validation.constraints.NotNull;

/**
 * 扩展点的内部定义，元数据
 *
 * @author NorthLan
 * @date 2021-02-22
 * @url https://noahlan.com
 */
@ToString
@Slf4j
@NoArgsConstructor
@Getter
public class ExtensionMeta implements IRegistryAware, IPrepareAware {
    private String code;
    private String name;
    private Class<? extends IDomainExtension> extClazz;
    private IDomainExtension extensionBean;

    public ExtensionMeta(IDomainExtension extensionBean) {
        this.extensionBean = extensionBean;
    }

    @Override
    public void prepare(@NotNull Object bean) {
        initialize(bean);
        InternalIndexer.prepare(this);
    }

    @Override
    public void registerBean(@NotNull Object bean) {
        initialize(bean);
        InternalIndexer.index(this);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void initialize(Object bean) {
        Extension extension = AopUtils.getAnnotation(bean, Extension.class);
        this.code = extension.code();
        this.name = extension.name();
        if (!(bean instanceof IDomainExtension)) {
            throw BootstrapException.ofMessage(bean.getClass().getCanonicalName(), " MUST implement IDomainExtension");
        }
        this.extensionBean = (IDomainExtension) bean;
        // this.extensionBean might be Xxx$EnhancerBySpringCGLIB if the extension uses AOP
        for (Class extensionBeanInterfaceClazz : AopUtils.getTarget(this.extensionBean).getClass().getInterfaces()) {
            if (extensionBeanInterfaceClazz.isInstance(extensionBean)) {
                this.extClazz = extensionBeanInterfaceClazz;

                log.debug("{} has ext instance:{}", this.extClazz.getCanonicalName(), this);
                break;
            }
        }
    }
}
