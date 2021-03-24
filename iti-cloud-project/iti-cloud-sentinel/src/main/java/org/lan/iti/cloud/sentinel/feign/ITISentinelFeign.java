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

package org.lan.iti.cloud.sentinel.feign;

import com.alibaba.cloud.sentinel.feign.SentinelContractHolder;
import feign.Contract;
import feign.Feign;
import feign.InvocationHandlerFactory;
import feign.Target;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

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
        return new ITISentinelFeign.Builder();
    }

    @SuppressWarnings("unchecked")
    public static final class Builder extends Feign.Builder {
        private Contract contract = new Contract.Default();

        private String feignClientName;

        public Builder feignClientName(String feignClientName) {
            this.feignClientName = feignClientName;
            return this;
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

        public <T> T target(Target<T> target, T fallback) {
            return build(fallback != null ? new FallbackFactory.Default<>(fallback) : null)
                    .newInstance(target);
        }

        public <T> T target(Target<T> target, FallbackFactory<? extends T> fallbackFactory) {
            return build(fallbackFactory).newInstance(target);
        }

        @Override
        public <T> T target(Target<T> target) {
            return build(null).newInstance(target);
        }

        public Feign build(final FallbackFactory<?> nullableFallbackFactory) {
            super.invocationHandlerFactory((target, dispatch) ->
                    new ITISentinelInvocationHandler(feignClientName, target, dispatch, nullableFallbackFactory));
//            super.invocationHandlerFactory(new InvocationHandlerFactory() {
//                @Override
//                public InvocationHandler create(Target target,
//                                                Map<Method, MethodHandler> dispatch) {
//                    // using reflect get fallback and fallbackFactory properties from
//                    // FeignClientFactoryBean because FeignClientFactoryBean is a package
//                    // level class, we can not use it in our package
//                    // 查找 FeignClient上的 降级策略
//
//                    Class<?> fallback = (Class<?>) getFieldValue(feignClientFactoryBean,
//                            "fallback");
//                    Class<?> fallbackFactory = (Class<?>) getFieldValue(feignClientFactoryBean,
//                            "fallbackFactory");
//                    String beanName = (String) getFieldValue(feignClientFactoryBean,
//                            "contextId");
//                    if (!StringUtils.hasText(beanName)) {
//                        beanName = (String) getFieldValue(feignClientFactoryBean, "name");
//                    }
//
//                    Object fallbackInstance;
//                    FallbackFactory<?> fallbackFactoryInstance;
//                    // check fallback and fallbackFactory properties
//                    if (void.class != fallback) {
//                        fallbackInstance = getFromContext(beanName, "fallback", fallback,
//                                target.type());
//                        return new ITISentinelInvocationHandler(target, dispatch,
//                                new FallbackFactory.Default<>(fallbackInstance));
//                    }
//                    if (void.class != fallbackFactory) {
//                        fallbackFactoryInstance = (FallbackFactory<?>) getFromContext(
//                                beanName, "fallbackFactory", fallbackFactory,
//                                FallbackFactory.class);
//                        return new ITISentinelInvocationHandler(target, dispatch,
//                                fallbackFactoryInstance);
//                    }
//
//                    // 添加默认的fallback工厂
//                    fallbackFactoryInstance = new ITIFallbackFactory(target);
//                    return new ITISentinelInvocationHandler(target, dispatch, fallbackFactoryInstance);
//                }
//
//                private Object getFromContext(String name, String type,
//                                              Class<?> fallbackType, Class<?> targetType) {
//                    Object fallbackInstance = feignContext.getInstance(name,
//                            fallbackType);
//                    if (fallbackInstance == null) {
//                        throw new IllegalStateException(String.format(
//                                "No %s instance of type %s found for feign client %s",
//                                type, fallbackType, name));
//                    }
//
//                    if (!targetType.isAssignableFrom(fallbackType)) {
//                        throw new IllegalStateException(String.format(
//                                "Incompatible %s instance. Fallback/fallbackFactory of type %s is not assignable to %s for feign client %s",
//                                type, fallbackType, targetType, name));
//                    }
//                    return fallbackInstance;
//                }
//            });

            super.contract(new SentinelContractHolder(contract));
            return super.build();
        }
    }
}
