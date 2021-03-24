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

package org.lan.iti.cloud.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.support.SpringContextHolder;
import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.framework.AopProxy;
import org.springframework.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author NorthLan
 * @date 2020-03-06
 * @url https://noahlan.com
 */
@UtilityClass
@Slf4j
public class AopUtils {

    /**
     * 获取Service的当前代理类
     * <p>
     * 一般用于在Service中调用Service方法（含缓存等注解）
     * </p>
     *
     * @param clazz 需代理的service类
     * @param <T>   代理类泛型
     * @return 代理类
     */
    @SuppressWarnings("unchecked")
    public <T> T currentServiceProxy(Class<T> clazz) {
        try {
            return (T) AopContext.currentProxy();
        } catch (IllegalStateException e) {
            if (clazz != null) {
                return SpringContextHolder.getBean(clazz);
            } else {
                throw e;
            }
        }
    }

    @SuppressWarnings({"unchecked", "CastCanBeRemovedNarrowingVariableType"})
    public <T extends Annotation> T getAnnotation(Object bean, Class<T> clazz) {
        Annotation annotation = bean.getClass().getAnnotation(clazz);
        if (annotation == null && org.springframework.aop.support.AopUtils.isAopProxy(bean)) {
            annotation = org.springframework.aop.support.AopUtils.getTargetClass(bean).getAnnotation(clazz);
        }
        return (T) annotation;
    }

    /**
     * 获取当前代理类对象的代理目标
     *
     * @param proxy 代理类对象
     * @return 代理目标类对象
     */
    @Nullable
    public Object getTarget(Object proxy) {
        if (!org.springframework.aop.support.AopUtils.isAopProxy(proxy)) {
            // 不是代理类
            return proxy;
        }
        try {
            // jdk or cglib
            if (org.springframework.aop.support.AopUtils.isJdkDynamicProxy(proxy)) {
                return getJdkDynamicProxyTargetObject(proxy);
            } else {
                return getCglibProxyTargetObject(proxy);
            }
        } catch (Exception e) {
            log.error("获取代理对象异常: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取Jdk动态代理目标对象
     *
     * @param proxy 代理类对象
     * @return jdk目标对象
     */
    @Nullable
    private Object getJdkDynamicProxyTargetObject(Object proxy) {
        try {
            Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
            // h 为private变量
            h.setAccessible(true);
            AopProxy aopProxy = (AopProxy) h.get(proxy);
            Field advised = aopProxy.getClass().getDeclaredField("advised");
            advised.setAccessible(true);
            return ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();
        } catch (Exception e) {
            log.debug("获取jdk代理对象发生异常：", e);
            return null;
        }
    }

    /**
     * 获取cglib动态代理目标对象
     *
     * @param proxy 代理类对象
     * @return cglib目标对象
     */
    @Nullable
    private Object getCglibProxyTargetObject(Object proxy) {
        try {
            Field h = proxy.getClass().getSuperclass().getDeclaredField("CGLIB$CALLBACK_0");
            // h 为private变量
            h.setAccessible(true);
            Object dynamicAdvisedInterceptor = h.get(proxy);
            Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
            advised.setAccessible(true);
            return ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
        } catch (Exception e) {
            log.debug("获取cglib代理对象发生异常：", e);
            return null;
        }
    }
}
