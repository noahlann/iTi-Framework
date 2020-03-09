/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.common.core.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * Spring的ApplicationContext的持有者,可以用静态方法的方式获取spring容器中的bean
 *
 * @author NorthLan
 * @date 2020-02-22
 * @url https://noahlan.com
 */
@Component
@Lazy(false)
@Slf4j
public class SpringContextHolder implements DisposableBean, ApplicationContextAware {
    @Getter
    private static ApplicationContext applicationContext;

    /**
     * 根据类型获取Beans
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        assertApplicationContext();
        return applicationContext.getBeansOfType(clazz);
    }

    /**
     * 通过bean名称获取Spring容器中的Bean
     * fast-fail
     *
     * @param beanName bean名称
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        assertApplicationContext();
        try {
            return (T) applicationContext.getBean(beanName);
        } catch (BeansException e) {
            log.warn("获取Bean: [{}] 错误,请检查bean是否已注入到Spring容器中!", beanName);
            throw e;
        }
    }

    /**
     * 通过bean名称获取Spring容器中的Bean
     * non-fast-fail
     *
     * @param beanName bean名称
     */
    public static <T> T getBeanOfNull(String beanName) {
        try {
            return getBean(beanName);
        } catch (BeansException e) {
            return null;
        }
    }

    /**
     * 通过类描述获取Spring容器中的Bean
     * fast-fail
     *
     * @param clazz 类描述
     */
    public static <T> T getBean(Class<T> clazz) {
        assertApplicationContext();
        try {
            return applicationContext.getBean(clazz);
        } catch (BeansException e) {
            log.warn("获取Bean: [{}] 错误,请检查bean是否已注入到Spring容器中!", clazz.getName());
            throw e;
        }
    }

    /**
     * 通过类描述获取Spring容器中的Bean
     * non-fast-fail
     *
     * @param clazz 类描述
     */
    public static <T> T getBeanOfNull(Class<T> clazz) {
        try {
            return getBean(clazz);
        } catch (BeansException e) {
            return null;
        }
    }

    /**
     * 发布事件
     *
     * @param event Spring事件
     */
    public static void publishEvent(ApplicationEvent event) {
        if (applicationContext == null) {
            return;
        }
        applicationContext.publishEvent(event);
    }

    private static void assertApplicationContext() {
        Assert.notNull(applicationContext, "applicationContext属性为null,请检查是否注入了SpringContextHolder");
    }

    @Override
    public void destroy() throws Exception {
        SpringContextHolder.applicationContext = null;
    }

    @Override
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.applicationContext = applicationContext;
    }
}
