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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.lan.iti.common.ddd.ext.IDomainExtension;
import org.lan.iti.common.ddd.model.IDomain;
import org.lan.iti.cloud.ddd.runtime.registry.InternalIndexer;
import org.lan.iti.cloud.ddd.runtime.registry.StepMeta;
import org.lan.iti.common.ddd.step.IDomainStep;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DDD框架核心类，定位核心对象
 *
 * @author NorthLan
 * @date 2021-02-24
 * @url https://noahlan.com
 */
@SuppressWarnings({"AlibabaClassNamingShouldBeCamel", "rawtypes", "unchecked"})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DDD {

    /**
     * 定位一个领域能力点实例.
     *
     * @param domainAbilityClazz 领域能力类型
     * @param <T>                领域能力类型
     * @return null if bug found：研发忘记使用注解DomainAbility了
     */
    @NotNull
    public static <T extends AbstractDomainAbility> T findAbility(@NotNull Class<? extends T> domainAbilityClazz) {
        return InternalIndexer.findDomainAbility(domainAbilityClazz);
    }

    /**
     * 根据步骤编号定位领域活动步骤.
     *
     * @param activityCode 所属领域活动编号
     * @param stepCodeList 领域步骤编号列表
     * @param <Step>       领域步骤类型
     * @return 如果没找到，会返回数量为0的非空列表
     */
    @NotNull
    public static <Step extends IDomainStep> List<Step> findSteps(@NotNull String activityCode, @NotNull List<String> stepCodeList) {
        List<StepMeta> stepMetas = InternalIndexer.findDomainSteps(activityCode, stepCodeList);
        List<Step> result = new ArrayList<>(stepMetas.size());
        for (StepMeta stepMeta : stepMetas) {
            result.add((Step) stepMeta.getStepBean());
        }

        return result;
    }

    /**
     * 绕过 {@link AbstractDomainAbility}，直接获取扩展点实例.
     *
     * <p>有的控制点：</p>
     * <ul>
     * <li>不需要默认的扩展点实现</li>
     * <li>不会有复杂的 {@link IReducer} 逻辑，取到第一个匹配的即可</li>
     * <li>没有很强的业务属性：它可能是出于技术考虑而抽象出来的，而不是业务抽象</li>
     * </ul>
     * <p>这些场景下，{@link AbstractDomainAbility} 显得有些多此一举，可直接使用 {@link DDD#firstExtension(Class, IDomain)}</p>
     *
     * @param extClazz 扩展点类型
     * @param model    领域模型，用于定位扩展点
     */
    @NotNull
    public static <Ext extends IDomainExtension, R> Ext firstExtension(@NotNull Class<Ext> extClazz, IDomain model) {
        ExtensionInvocationHandler<Ext, R> proxy = new ExtensionInvocationHandler(extClazz, model, null, null, 0);
        return proxy.createProxy();
    }

    /**
     * 定位某一个领域步骤实例.
     *
     * @param activityCode 所属领域活动编号
     * @param stepCode     步骤编号
     * @param <Step>       领域步骤类型
     * @return 如果找不到，则返回null
     */
    public static <Step extends IDomainStep> Step getStep(@NotNull String activityCode, @NotNull String stepCode) {
        List<Step> steps = findSteps(activityCode, Collections.singletonList(stepCode));
        if (steps.size() == 1) {
            return steps.get(0);
        }
        return null;
    }
}
