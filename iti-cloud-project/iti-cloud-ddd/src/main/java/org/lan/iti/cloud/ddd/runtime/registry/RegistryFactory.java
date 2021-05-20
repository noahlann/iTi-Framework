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

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.ddd.annotation.*;
import org.lan.iti.cloud.ddd.constants.DDDConstants;
import org.lan.iti.common.core.constants.CommonConstants;
import org.lan.iti.common.core.exception.BootstrapException;
import org.lan.iti.common.ddd.ext.IPlugable;
import org.slf4j.MDC;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author NorthLan
 * @date 2021-02-24
 * @url https://noahlan.com
 */
@Slf4j
@Deprecated
public class RegistryFactory implements InitializingBean {

    /**
     * 合法的注册入口
     * <pre>
     *     有序的，因为他们之间有时间依赖关系
     * </pre>
     */
    static List<RegistryEntry> validRegistryEntries = new ArrayList<>();

    private static final Map<Class<? extends Annotation>, PrepareEntry> VALID_PREPARE_ENTRIES = new HashMap<>(3);

    @Override
    public void afterPropertiesSet() throws Exception {
        try (MDC.MDCCloseable ignore = MDC.putCloseable(CommonConstants.PREFIX_KEY, DDDConstants.MDC_PREFIX)) {
            log.info("setup the discoverable Spring beans...");
        }

        // 注册Domain，是为了可视化，避免漏掉某些支撑域
        validRegistryEntries.add(new RegistryEntry(Domain.class, DomainMeta::new));
        validRegistryEntries.add(new RegistryEntry(DomainService.class, DomainServiceMeta::new));
        validRegistryEntries.add(new RegistryEntry(Specification.class, SpecificationMeta::new));
        validRegistryEntries.add(new RegistryEntry(Step.class, StepMeta::new));
        validRegistryEntries.add(new RegistryEntry(DomainAbility.class, DomainAbilityMeta::new));
        validRegistryEntries.add(new RegistryEntry(Policy.class, PolicyMeta::new));
        validRegistryEntries.add(new RegistryEntry(Partner.class, PartnerMeta::new));
        validRegistryEntries.add(new RegistryEntry(Pattern.class, PatternMeta::new));
        validRegistryEntries.add(new RegistryEntry(Extension.class, ExtensionMeta::new));

        VALID_PREPARE_ENTRIES.put(Partner.class, new PrepareEntry(PartnerMeta::new));
        VALID_PREPARE_ENTRIES.put(Extension.class, new PrepareEntry(ExtensionMeta::new));
    }

    public void register(ApplicationContext applicationContext) {
        MDC.MDCCloseable mdc = MDC.putCloseable(CommonConstants.PREFIX_KEY, DDDConstants.MDC_PREFIX);
        for (RegistryEntry registryEntry : validRegistryEntries) {
            log.info("register {}'s ...", registryEntry.annotation.getSimpleName());

            for (Object springBean : applicationContext.getBeansWithAnnotation(registryEntry.annotation).values()) {
                registryEntry.create().registerBean(springBean);
            }
        }
        InternalIndexer.postIndexing();
        mdc.close();
    }

    public static void preparePlugins(Class<? extends Annotation> annotation, Object bean) {
        if (!(bean instanceof IPlugable)) {
            throw BootstrapException.ofMessage(bean.getClass().getCanonicalName() + " must be IPlugable");
        }

        PrepareEntry prepareEntry = VALID_PREPARE_ENTRIES.get(annotation);
        if (prepareEntry == null) {
            throw BootstrapException.ofMessage(annotation.getCanonicalName() + " not supported");
        }

        prepareEntry.create().prepare(bean);
    }

    private static class RegistryEntry {
        private final Class<? extends Annotation> annotation;
        private final Supplier<IRegistryAware> supplier;

        RegistryEntry(Class<? extends Annotation> annotation, Supplier<IRegistryAware> supplier) {
            this.annotation = annotation;
            this.supplier = supplier;
        }

        IRegistryAware create() {
            return supplier.get();
        }
    }

    private static class PrepareEntry {
        private final Supplier<IPrepareAware> supplier;

        PrepareEntry(Supplier<IPrepareAware> supplier) {
            this.supplier = supplier;
        }

        IPrepareAware create() {
            return supplier.get();
        }
    }
}
