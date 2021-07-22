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
import java.util.List;

/**
 * 修订后续步骤的异常.
 * <p>
 * <p>配合{@link IDomainStep#execute(IDomain)}的异常使用，在某一步骤抛出该异常来修订后续步骤</p>
 * <p>可能产生的死循环(a -> b(revise) -> a)，由使用者负责，暂时不提供dead loop检测：因为即使检测到也不知道如何处理，它本身就是bug</p>
 * <p>有的最佳实践说：不要使用异常控制流程。但在这里，它更有效，不要太在意最佳实践的说法</p>
 * <p>IMPORTANT: 不要在领域层异常直接实现该接口，应该创建新的异常类，否则会与步骤的回滚机制冲突！推荐直接使用{@link ReviseStepsException}</p>
 *
 * @author NorthLan
 * @date 2021-02-07
 * @url https://noahlan.com
 */
@Deprecated
public interface IReviseStepsException {
    /**
     * 修订后的后续步骤编号
     *
     * @return 具有顺序的步骤编号
     */
    @NotNull
    List<String> subsequentSteps();
}
