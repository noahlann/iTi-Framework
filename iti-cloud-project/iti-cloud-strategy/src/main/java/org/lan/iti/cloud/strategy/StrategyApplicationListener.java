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

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.strategy.extension.StrategyExtensionParameterBuilder;
import org.lan.iti.common.extension.ExtensionLoader;
import org.lan.iti.common.extension.adapter.parameter.ExtensionAdapterParameter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author NorthLan
 * @date 2021-05-19
 * @url https://noahlan.com
 */
@Slf4j
public class StrategyApplicationListener implements ApplicationContextAware {
    private final AtomicBoolean once = new AtomicBoolean();

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        if (!once.compareAndSet(false, true)) {
            log.warn("register applicationContext more than once, ignored!");
            return;
        }

        ExtensionAdapterParameter parameter = ExtensionAdapterParameter.builder()
                .custom(StrategyExtensionParameterBuilder::new)
                .applicationContext(applicationContext)
                .build();
        ExtensionLoader.getAdapterFactory().addParameter(parameter);
    }
}
