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

import javax.validation.constraints.NotNull;

/**
 * 可取消的领域活动步骤接口
 * <p>
 * <p>Sagas模式</p>
 * <p>Best effort</p>
 *
 * @author NorthLan
 * @date 2021-02-07
 * @url https://noahlan.com
 */
@Deprecated
public interface IRevokableDomainStep<Model extends IDomain, Ex extends RuntimeException> extends IDomainStep<Model, Ex> {

    /**
     * 执行本步骤的回滚操作，进行冲正.
     * <p>
     * <p>Best effort就好，Sagas模式并不能严格保证一致性</p>
     *
     * @param model 领域模型
     * @param cause {@link IDomainStep#execute(IDomain)}执行过程中抛出的异常，即回滚原因
     */
    void rollback(@NotNull Model model, @NotNull Ex cause);
}
