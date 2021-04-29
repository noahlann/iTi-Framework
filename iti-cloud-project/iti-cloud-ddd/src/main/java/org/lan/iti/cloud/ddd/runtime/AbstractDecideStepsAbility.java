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

import org.lan.iti.common.ddd.ext.IDecideStepsExt;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

/**
 * 基础决策领域步骤的能力.
 *
 * @author NorthLan
 * @date 2021-02-24
 * @url https://noahlan.com
 * @deprecated 编排能力不再有
 */
@Deprecated
public abstract class AbstractDecideStepsAbility extends AbstractDomainAbility<IDecideStepsExt> {
    private static final IDecideStepsExt DEFAULT_EXT = new EmptyExt();

    /**
     * 根据 给定条件参数 和领域活动码决定需要执行哪些领域步骤.
     *
     * @param params       条件参数
     * @param activityCode 领域活动码
     * @return step code list
     */
    public List<String> decideSteps(@NotNull Object params, String activityCode) {
        return firstExtension(params).decideSteps(params, activityCode);
    }

    @Override
    public IDecideStepsExt defaultExtension(@NotNull Object params) {
        return DEFAULT_EXT;
    }

    private static class EmptyExt implements IDecideStepsExt {
        private static final List<String> EMPTY_STEPS = Collections.emptyList();

        @Override
        public List<String> decideSteps(@NotNull Object params, @NotNull String activityCode) {
            return EMPTY_STEPS;
        }
    }
}
