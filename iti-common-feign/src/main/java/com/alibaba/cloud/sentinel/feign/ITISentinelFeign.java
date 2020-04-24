/*
 *
 *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
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

package com.alibaba.cloud.sentinel.feign;

import feign.Contract;
import feign.Feign;
import feign.InvocationHandlerFactory;
import feign.Target;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.feign.fallback.ITIFallbackFactory;
import org.lan.iti.common.feign.properties.ITIFeignProperties;
import org.springframework.beans.BeansException;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 扩展 ITISentinelFeign
 * <p>
 * 添加默认 fallback
 * 无 fallback 异常处理,解决FeignClientBuilder无法build问题
 * </p>
 *
 * @author NorthLan
 * @date 2020-04-22
 * @url https://noahlan.com
 */
@Slf4j
public class ITISentinelFeign {
    private ITISentinelFeign() {
    }

    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("unchecked")
    public static final class Builder extends Feign.Builder
            implements ApplicationContextAware {
        private Contract contract = new Contract.Default();
        private ApplicationContext applicationContext;
        private FeignContext feignContext;

        @Override
        public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
            this.feignContext = this.applicationContext.getBean(FeignContext.class);
        }

        @Override
        public Feign.Builder invocationHandlerFactory(InvocationHandlerFactory invocationHandlerFactory) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Feign.Builder contract(Contract contract) {
            this.contract = contract;
            return this;
        }

        @Override
        public Feign build() {
            super.invocationHandlerFactory(new InvocationHandlerFactory() {
                @Override
                public InvocationHandler create(Target target,
                                                Map<Method, MethodHandler> dispatch) {
                    // using reflect get fallback and fallbackFactory properties from
                    // FeignClientFactoryBean because FeignClientFactoryBean is a package
                    // level class, we can not use it in our package
                    Object feignClientFactoryBean = null;
                    ITIFeignProperties properties = Builder.this.applicationContext.getBean(ITIFeignProperties.class);
                    try {
                        feignClientFactoryBean = Builder.this.applicationContext
                                .getBean("&" + target.type().getName());
                    } catch (BeansException e) {
                        log.warn("获取 feignClientFactoryBean:{} 出错,允许程序继续运行", "&" + target.type().getName());
                        if (properties.isFallback()) {
                            // 添加默认的fallback工厂
                            FallbackFactory<?> fallbackFactoryInstance = new ITIFallbackFactory<>(target);
                            return new SentinelInvocationHandler(target, dispatch, fallbackFactoryInstance);
                        } else {
                            return new SentinelInvocationHandler(target, dispatch);
                        }
                    }

                    Class<?> fallback = (Class<?>) getFieldValue(feignClientFactoryBean,
                            "fallback");
                    Class<?> fallbackFactory = (Class<?>) getFieldValue(feignClientFactoryBean,
                            "fallbackFactory");
                    String beanName = (String) getFieldValue(feignClientFactoryBean,
                            "contextId");
                    if (!StringUtils.hasText(beanName)) {
                        beanName = (String) getFieldValue(feignClientFactoryBean, "name");
                    }

                    Object fallbackInstance;
                    FallbackFactory<?> fallbackFactoryInstance;
                    // check fallback and fallbackFactory properties
                    if (void.class != fallback) {
                        fallbackInstance = getFromContext(beanName, "fallback", fallback,
                                target.type());
                        return new SentinelInvocationHandler(target, dispatch,
                                new FallbackFactory.Default<>(fallbackInstance));
                    }
                    if (void.class != fallbackFactory) {
                        fallbackFactoryInstance = (FallbackFactory<?>) getFromContext(
                                beanName, "fallbackFactory", fallbackFactory,
                                FallbackFactory.class);
                        return new SentinelInvocationHandler(target, dispatch,
                                fallbackFactoryInstance);
                    }

                    if (properties.isFallback()) {
                        // 添加默认的fallback工厂
                        fallbackFactoryInstance = new ITIFallbackFactory<>(target);
                        return new SentinelInvocationHandler(target, dispatch, fallbackFactoryInstance);
                    } else {
                        return new SentinelInvocationHandler(target, dispatch);
                    }
                }

                private Object getFromContext(String name, String type,
                                              Class<?> fallbackType, Class<?> targetType) {
                    Object fallbackInstance = feignContext.getInstance(name,
                            fallbackType);
                    if (fallbackInstance == null) {
                        throw new IllegalStateException(String.format(
                                "No %s instance of type %s found for feign client %s",
                                type, fallbackType, name));
                    }

                    if (!targetType.isAssignableFrom(fallbackType)) {
                        throw new IllegalStateException(String.format(
                                "Incompatible %s instance. Fallback/fallbackFactory of type %s is not assignable to %s for feign client %s",
                                type, fallbackType, targetType, name));
                    }
                    return fallbackInstance;
                }
            });

            super.contract(new SentinelContractHolder(contract));
            return super.build();
        }

        private Object getFieldValue(Object instance, String fieldName) {
            Field field = ReflectionUtils.findField(instance.getClass(), fieldName);
            if (field == null) {
                return null;
            }
            field.setAccessible(true);
            try {
                return field.get(instance);
            } catch (IllegalAccessException e) {
                // ignore
            }
            return null;
        }
    }
}
