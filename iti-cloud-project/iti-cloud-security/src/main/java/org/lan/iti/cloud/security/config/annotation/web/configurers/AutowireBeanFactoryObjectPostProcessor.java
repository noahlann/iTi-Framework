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

package org.lan.iti.cloud.security.config.annotation.web.configurers;

import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.security.config.annotation.ObjectPostProcessor;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows registering Objects to participate with an {@link AutowireCapableBeanFactory}'s
 * post processing of {@link Aware} methods, {@link InitializingBean#afterPropertiesSet()}
 * , and {@link DisposableBean#destroy()}.
 *
 * @author NorthLan
 * @date 2021/10/18
 * @url https://blog.noahlan.com
 */
@Slf4j
public class AutowireBeanFactoryObjectPostProcessor
        implements ObjectPostProcessor<Object>, DisposableBean, SmartInitializingSingleton {

    private final AutowireCapableBeanFactory autowireBeanFactory;
    private final List<DisposableBean> disposableBeans = new ArrayList<>();

    private final List<SmartInitializingSingleton> smartSingletons = new ArrayList<>();

    AutowireBeanFactoryObjectPostProcessor(AutowireCapableBeanFactory autowireBeanFactory) {
        Assert.notNull(autowireBeanFactory, "autowireBeanFactory cannot be null");
        this.autowireBeanFactory = autowireBeanFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T postProcess(T object) {
        if (object == null) {
            return null;
        }
        T result = null;
        try {
            result = (T) this.autowireBeanFactory.initializeBean(object, object.toString());
        } catch (RuntimeException ex) {
            Class<?> type = object.getClass();
            throw new RuntimeException("Could not postProcess " + object + " of type " + type, ex);
        }
        this.autowireBeanFactory.autowireBean(object);
        if (result instanceof DisposableBean) {
            this.disposableBeans.add((DisposableBean) result);
        }
        if (result instanceof SmartInitializingSingleton) {
            this.smartSingletons.add((SmartInitializingSingleton) result);
        }
        return result;
    }

    @Override
    public void afterSingletonsInstantiated() {
        for (SmartInitializingSingleton singleton : this.smartSingletons) {
            singleton.afterSingletonsInstantiated();
        }
    }

    @Override
    public void destroy() {
        for (DisposableBean disposable : this.disposableBeans) {
            try {
                disposable.destroy();
            } catch (Exception ex) {
                log.error("destroy exception", ex);
            }
        }
    }
}
