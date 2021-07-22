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

package org.lan.iti.common.ddd.ext;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 用于编排领域步骤的扩展点
 *
 * @author NorthLan
 * @date 2021-02-08
 * @url https://noahlan.com
 */
@Deprecated
public interface IDecideStepsExt extends IDomainExtension {

    /**
     * 根据领域模型和领域活动码决定需要执行哪些领域步骤
     *
     * @param params       条件参数
     * @param activityCode 领域活动码
     * @return stepCode List
     */
    @NotNull List<String> decideSteps(@NotNull Object params, @NotNull String activityCode);
}
