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

package org.lan.iti.cloud.ddd.runtime;

import org.lan.iti.cloud.ddd.runtime.registry.InternalIndexer;
import org.lan.iti.common.ddd.ext.IDomainExtension;
import org.lan.iti.common.ddd.model.IDomain;
import org.lan.iti.common.ddd.model.IDomainService;

import javax.validation.constraints.NotNull;

/**
 * 基础领域能力，是一种业务多态能力：即，业务扩展能力
 * <p>
 * <p>{@code AbstractDomainAbility}是最小粒度的{@link IDomainService}，只负责一个扩展点的编排</p>
 *
 * @author NorthLan
 * @date 2021-02-22
 * @url https://noahlan.com
 */
@Deprecated
public abstract class AbstractDomainAbility<Ext extends IDomainExtension> implements IDomainService {

    /**
     * 定位指定的扩展点实例.
     * <p>
     * <p>可以通过{@link IReducer}实现多个扩展点的执行</p>
     *
     * @param params  条件参数
     * @param reducer 扩展点执行的归约器 如果为空，则等同于{@link #firstExtension(IDomain)}
     * @param <R>     扩展点方法的返回值类型
     * @return null if not found
     */
    @SuppressWarnings("unchecked")
    protected <R> Ext getExtension(@NotNull Object params, IReducer<R> reducer) {
        Class<? extends IDomainExtension> extClazz = InternalIndexer.getDomainAbilityExtDeclaration(this.getClass());
        return findExtension((Class<Ext>) extClazz, params, reducer, defaultExtension(params), 0);
    }

    /**
     * 找到第一个符合条件的扩展点实例.
     * <p>
     * <p>这表示：扩展点实例之间是互斥的，无法叠加的</p>
     * <p>如果需要根据扩展点执行结果来找第一个匹配的扩展点实例，请使用{@link #getExtension(IDomain, IReducer)}</p>
     *
     * @param params 条件参数
     * @return null if not found
     */
    protected Ext firstExtension(@NotNull Object params) {
        return firstExtension(params, 0);
    }

    /**
     * 找到第一个符合条件的扩展点实例，并指定扩展点最大执行时长，超时抛出{@link java.util.concurrent.TimeoutException}.
     * <p>
     * <p>这表示：扩展点实例之间是互斥的，无法叠加的</p>
     * <p>如果需要根据扩展点执行结果来找第一个匹配的扩展点实例，请使用{@link #getExtension(IDomain, IReducer)}</p>
     *
     * @param params      条件参数
     * @param timeoutInMs 执行扩展点的超时时间，in ms；如果超时，会强行终止扩展点的执行
     * @return null if not found
     */
    @SuppressWarnings("unchecked")
    protected Ext firstExtension(@NotNull Object params, int timeoutInMs) {
        Class<? extends IDomainExtension> extClazz = InternalIndexer.getDomainAbilityExtDeclaration(this.getClass());
        return findExtension((Class<Ext>) extClazz, params, null, defaultExtension(params), timeoutInMs);
    }

    /**
     * 默认扩展，当且仅当无其它扩展时调用
     *
     * @param params 条件参数
     * @return 扩展点实例
     */
    public abstract Ext defaultExtension(@NotNull Object params);

    private <E extends IDomainExtension, R> E findExtension(@NotNull Class<E> extClazz, @NotNull Object params, IReducer<R> reducer, E defaultExt, int timeoutInMs) {
        ExtensionInvocationHandler<E, R> proxy = new ExtensionInvocationHandler<>(extClazz, params, reducer, defaultExt, timeoutInMs);
        return proxy.createProxy();
    }
}
