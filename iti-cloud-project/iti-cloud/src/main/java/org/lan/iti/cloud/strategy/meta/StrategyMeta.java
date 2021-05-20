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

import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.strategy.IRegistryAware;
import org.lan.iti.cloud.strategy.IStrategy;
import org.lan.iti.cloud.strategy.annotation.Strategy;
import org.lan.iti.cloud.strategy.exception.StrategyException;
import org.lan.iti.cloud.util.AopUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * StrategyMeta
 *
 * @author NorthLan
 * @date 2021-05-15
 * @url https://noahlan.com
 */
@NoArgsConstructor
@Getter
@Slf4j
public class StrategyMeta implements IRegistryAware, IStrategy {
    private String code;
    private String name;
    private int priority = Ordered.LOWEST_PRECEDENCE;
    private Class<? extends IStrategy> strategyClazz;
    private IStrategy strategyBean;

    @Override
    public boolean matches(Object identify) {
        return strategyBean.matches(identify);
    }

    @Override
    public void registerBean(Object bean) {
        initialize(bean);

        StrategyMetaIndexer.index(this);
    }

    private void initialize(Object bean) {
        Strategy strategy = AopUtils.getAnnotation(bean, Strategy.class);
        String code = strategy.code();
        if (StrUtil.isBlank(code)) {
            code = StrUtil.lowerFirst(bean.getClass().getSimpleName());
        }
        this.code = code;
        this.name = strategy.name();

        Order order = AopUtils.getAnnotation(bean, Order.class);
        if (order != null) {
            this.priority = order.value();
        }
        if (!(bean instanceof IStrategy)) {
            throw StrategyException.ofMessage("{} MUST implements IStrategy", bean.getClass().getCanonicalName());
        }
        this.strategyBean = (IStrategy) bean;

        // this.strategyBean might be Xxx$EnhancerBySpringCGLIB if the strategy uses AOP
        for (Class extensionBeanInterfaceClazz : AopUtils.getTarget(this.strategyBean).getClass().getInterfaces()) {
            if (extensionBeanInterfaceClazz.isInstance(strategyBean)) {
                this.strategyClazz = extensionBeanInterfaceClazz;

                log.debug("{} has ext instance:{}", this.strategyClazz.getCanonicalName(), this);
                break;
            }
        }
    }
}
