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

import java.util.List;

/**
 * 扩展点执行的归约器，控制扩展点多个实例的叠加.
 * <p>
 * <p>采用MapReduce模式</p>
 * <p>Accepts a list of extensions and a reduce function to produce the result.</p>
 * <p>It basically says the providers can coexist, but you need to coordinate their results.</p>
 *
 * @author NorthLan
 * @date 2021-02-22
 * @url https://noahlan.com
 */
@Deprecated
public interface IReducer<R> {

    /**
     * 扩展点执行结果规约
     *
     * @param accumulatedResults 目前已执行的所有扩展点的结果集
     * @return 计算后的扩展点结果
     */
    R reduce(List<R> accumulatedResults);

    /**
     * 判断扩展点执行是否该停下来.
     *
     * @param accumulatedResults 目前已经执行的所有扩展点的结果集
     * @return 是否应该停下后续扩展点的执行
     */
    boolean shouldStop(List<R> accumulatedResults);
}
