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

package org.lan.iti.cloud.extension;

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.extension.ExtensionLoader;
import org.lan.iti.common.extension.adapter.parameter.ExtensionAdapterParameter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Extension - Spring 初始化过程
 * <p>
 * SpringBean 的执行顺序
 * <ul>
 *     <li>{@link ApplicationContextAware#setApplicationContext(ApplicationContext)}</li>
 *     <li>{@link javax.annotation.PostConstruct}</li>
 *     <li>{@link InitializingBean#afterPropertiesSet()}</li>
 *     <li>{@link ApplicationListener<ContextRefreshedEvent>}</li>
 * </ul>
 *
 * @author NorthLan
 * @date 2021-07-11
 * @url https://noahlan.com
 */
@Slf4j
public class SpringExtensionApplicationListener implements ApplicationListener<ContextRefreshedEvent>, ApplicationContextAware {
    private final AtomicBoolean once = new AtomicBoolean();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (!once.compareAndSet(false, true)) {
            log.warn("register applicationContext more than once, ignored!");
            return;
        }

        // Extension - spring 扫描 @Extension 配置
        ExtensionAdapterParameter parameter = ExtensionAdapterParameter.builder()
                .custom(SpringExtensionParameterBuilder::new)
                .applicationContext(applicationContext)
                .build();
        ExtensionLoader.getAdapterFactory().addParameter(parameter);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        // Spring 启动adapter初始化过程
        ExtensionLoader.getAdapterFactory().init();
    }
}
