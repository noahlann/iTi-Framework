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

package org.lan.iti.common.ddd.step;

import org.lan.iti.common.ddd.model.IDomain;
import org.lan.iti.common.ddd.model.IDomainService;

import javax.validation.constraints.NotNull;

/**
 * 领域活动(业务活动)的步骤，一种可以被编排的领域服务.
 * <p>
 * <p>一个领域活动(例如：接单是一个领域活动)是由多个步骤组成的.</p>
 * <p>步骤，相当于隐藏业务细节，宏观对业务活动的抽象.</p>
 * <p>
 * <p>普通的领域服务是业务系统主动声明接口并实现的，相当于主动提供服务.</p>
 * <p>而{@code IDomainStep}是框架层声明的接口，由业务系统在领域层实现，相当于被动提供服务.</p>
 *
 * @param <Model> 领域模型
 * @param <Ex>    中断步骤执行或改变后续步骤的异常
 * @author NorthLan
 * @date 2021-02-07
 * @url https://noahlan.com
 */
@Deprecated
public interface IDomainStep<Model extends IDomain, Ex extends RuntimeException> extends IDomainService {
    /**
     * 执行步骤
     *
     * @param model 领域模型
     * @throws Ex 中断步骤执行或改变后续步骤的异常
     */
    void execute(@NotNull Model model) throws Ex;

    /**
     * 所属的领域活动编号
     * <p>
     * <p>每一种领域活动，都具有一个唯一编号</p>
     *
     * @return 所属的领域活动编号
     */
    @NotNull
    String activityCode();

    /**
     * 本步骤编号
     *
     * @return 步骤编号
     */
    @NotNull
    String stepCode();
}
