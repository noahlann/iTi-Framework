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

package org.lan.iti.cloud.strategy;

import cn.hutool.core.collection.CollUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lan.iti.cloud.strategy.annotation.Strategy;
import org.lan.iti.cloud.strategy.meta.StrategyMeta;
import org.lan.iti.cloud.strategy.meta.StrategyMetaIndexer;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author NorthLan
 * @date 2021-05-19
 * @url https://noahlan.com
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RegistryFactory {

    private static final List<RegistryEntry> REGISTRY_ENTRIES = new ArrayList<>();

    public static void init() {
        REGISTRY_ENTRIES.add(new RegistryEntry(Strategy.class, StrategyMeta::new));
    }

    public static void register(ApplicationContext applicationContext) {
        for (RegistryEntry registryEntry : REGISTRY_ENTRIES) {
            registerWithBeans(registryEntry, applicationContext.getBeansWithAnnotation(registryEntry.getAnnotation()).values());
        }
        StrategyMetaIndexer.postIndexing();
    }

    public static void register(Function<RegistryEntry, Collection<Object>> singleton) {
        for (RegistryEntry registryEntry : REGISTRY_ENTRIES) {
            registerWithBeans(registryEntry, singleton.apply(registryEntry));
        }
        StrategyMetaIndexer.postIndexing();
    }

    private static void registerWithBeans(RegistryEntry entry, Collection<Object> beans) {
        if (CollUtil.isNotEmpty(beans)) {
            for (Object bean : beans) {
                entry.create().registerBean(bean);
            }
        }
    }

    public static class RegistryEntry {
        @Getter
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
}
