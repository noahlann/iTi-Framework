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


import feign.Feign;
import feign.Target;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClientFactoryBean;
import org.springframework.cloud.openfeign.FeignContext;
import org.springframework.cloud.openfeign.Targeter;
import org.springframework.util.StringUtils;

/**
 * Sentinel Target
 * <p>{@see org.springframework.cloud.openfeign.FeignCircuitBreakerTargeter}</p>
 * <p>解决1.8.1版本不支持SpringCloud SR10及以上版本(如: 2020)的问题</p>
 * <p>自定义扩展默认fallback实现</p>
 *
 * @author NorthLan
 * @date 2021-03-19
 * @url https://noahlan.com
 */
@SuppressWarnings("unchecked")
public class FeignSentinelTarget implements Targeter {

    @Override
    public <T> T target(FeignClientFactoryBean factory, Feign.Builder feign, FeignContext context, Target.HardCodedTarget<T> target) {
        if (!(feign instanceof ITISentinelFeign.Builder)) {
            return feign.target(target);
        }
        ITISentinelFeign.Builder builder = (ITISentinelFeign.Builder) feign;

        String name = !StringUtils.hasText(factory.getContextId()) ? factory.getName() : factory.getContextId();
        Class<?> fallback = factory.getFallback();
        if (fallback != void.class) {
            return targetWithFallback(name, context, target, builder, fallback);
        }
        Class<?> fallbackFactory = factory.getFallbackFactory();
        if (fallbackFactory != void.class) {
            return targetWithFallbackFactory(name, context, target, builder, fallbackFactory);
        }
        // 是否注入默认fallback
        // targetWithDefaultFallback
        return builder(name, builder).target(target);
    }

    private <T> T targetWithFallbackFactory(String feignClientName,
                                            FeignContext context,
                                            Target.HardCodedTarget<T> target,
                                            ITISentinelFeign.Builder builder,
                                            Class<?> fallbackFactoryClass) {
        FallbackFactory<? extends T> fallbackFactory = (FallbackFactory<? extends T>) getFromContext("fallbackFactory",
                feignClientName, context, fallbackFactoryClass, FallbackFactory.class);
        return builder(feignClientName, builder).target(target, fallbackFactory);
    }

    private <T> T targetWithFallback(String feignClientName,
                                     FeignContext context,
                                     Target.HardCodedTarget<T> target,
                                     ITISentinelFeign.Builder builder,
                                     Class<?> fallback) {
        T fallbackInstance = getFromContext("fallback", feignClientName, context, fallback, target.type());
        return builder(feignClientName, builder).target(target, fallbackInstance);
    }

    private <T> T getFromContext(String fallbackMechanism, String feignClientName, FeignContext context,
                                 Class<?> beanType, Class<T> targetType) {
        Object fallbackInstance = context.getInstance(feignClientName, beanType);
        if (fallbackInstance == null) {
            throw new IllegalStateException(
                    String.format("No " + fallbackMechanism + " instance of type %s found for feign client %s",
                            beanType, feignClientName));
        }

        if (!targetType.isAssignableFrom(beanType)) {
            throw new IllegalStateException(String.format("Incompatible " + fallbackMechanism
                            + " instance. Fallback/fallbackFactory of type %s is not assignable to %s for feign client %s",
                    beanType, targetType, feignClientName));
        }
        return (T) fallbackInstance;
    }

    private ITISentinelFeign.Builder builder(String feignClientName, ITISentinelFeign.Builder builder) {
        return builder.feignClientName(feignClientName);
    }
}
