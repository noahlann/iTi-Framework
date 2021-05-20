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

package org.lan.iti.cloud.strategy.meta;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.lan.iti.cloud.strategy.IStrategy;
import org.lan.iti.cloud.strategy.exception.StrategyException;

import java.util.*;

/**
 * @author NorthLan
 * @date 2021-05-18
 * @url https://noahlan.com
 */
@Slf4j
public class StrategyMetaIndexer {

    static final Map<String, StrategyMeta> STRATEGIES_META_MAP = new HashMap<>();
    static final Map<Class<? extends IStrategy>, List<StrategyMeta>> SORTED_STRATEGIES_META_MAP = new HashMap<>();

    static void index(StrategyMeta meta) {
        if (STRATEGIES_META_MAP.containsKey(meta.getCode())) {
            throw StrategyException.ofMessage("duplicated strategy code: ", meta.getCode());
        }
        STRATEGIES_META_MAP.put(meta.getCode(), meta);
        log.debug("indexed {}", meta);
    }

    public static void postIndexing() {
        for (StrategyMeta meta : STRATEGIES_META_MAP.values()) {
            Class<? extends IStrategy> strategyClazz = meta.getStrategyClazz();
            if (!SORTED_STRATEGIES_META_MAP.containsKey(strategyClazz)) {
                SORTED_STRATEGIES_META_MAP.put(strategyClazz, new ArrayList<>());
            }
            SORTED_STRATEGIES_META_MAP.get(strategyClazz).add(meta);
        }
        // sort and antiDup of defaults
        for (List<StrategyMeta> metas : SORTED_STRATEGIES_META_MAP.values()) {
            metas.sort(Comparator.comparingInt(StrategyMeta::getPriority));
        }

        // 运行时不使用
        STRATEGIES_META_MAP.clear();
    }

    public static List<StrategyMeta> findEffectiveStrategies(
            @NotNull Class<? extends IStrategy> strategyClazz,
            @NotNull Object identify,
            boolean firstStop) {
        List<StrategyMeta> effectiveExtensions = new LinkedList<>();

        List<StrategyMeta> metas = SORTED_STRATEGIES_META_MAP.get(strategyClazz);
        for (StrategyMeta strategyMeta : metas) {
            if (!strategyMeta.matches(identify)) {
                continue;
            }
            effectiveExtensions.add(strategyMeta);

            if (firstStop && !effectiveExtensions.isEmpty()) {
                return effectiveExtensions;
            }
        }
        return effectiveExtensions;
    }
}
