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
import org.lan.iti.cloud.ddd.constants.DDDConstants;
import org.lan.iti.cloud.ddd.runtime.IStartupListener;
import org.lan.iti.common.core.constants.CommonConstants;
import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * DDD启动器
 *
 * @author NorthLan
 * @date 2021-02-24
 * @url https://noahlan.com
 */
@SuppressWarnings("AlibabaClassNamingShouldBeCamel")
@Slf4j
public class DDDBootstrap implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {
    private final AtomicBoolean once = new AtomicBoolean();

    private static ApplicationContext applicationContext;

    @Resource
    private RegistryFactory registryFactory;

    @Autowired(required = false)
    private List<IStartupListener> startupListeners;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        MDC.MDCCloseable mdc = MDC.putCloseable(CommonConstants.PREFIX_KEY, DDDConstants.MDC_PREFIX);
        if (!once.compareAndSet(false, true)) {
            log.warn("register applicationContext more than once, ignored!");
            mdc.close();
            return;
        }

        long t0 = System.nanoTime();

        log.info("starting Spring, register DDD beans...");
        registryFactory.register(applicationContext);

        log.info("all DDD beans registered, cost {}ms", (System.nanoTime() - t0) / 1000_000);

        DDDBootstrap.applicationContext = applicationContext;

        mdc.close();
    }

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        if (event.getApplicationContext().equals(applicationContext)) {
            log.info("Spring started complete!");
            if (!CollectionUtils.isEmpty(startupListeners)) {
                for (IStartupListener startupListener : startupListeners) {
                    log.debug("calling IStartupListener: {}", startupListener.getClass().getCanonicalName());
                    startupListener.onStartComplete();
                    log.debug("called IStartupListener: {}", startupListener.getClass().getCanonicalName());
                }
            }
        } else {
            log.info("Spring reloaded complete!");
        }
    }
}
