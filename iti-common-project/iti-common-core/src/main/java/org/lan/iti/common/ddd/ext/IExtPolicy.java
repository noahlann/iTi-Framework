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

/**
 * 扩展点动态定位策略
 *
 * <p>不同于 {@link IIdentityResolver} 的静态绑定，扩展点定位策略是动态的.</p>
 * <p>每一个扩展点定位策略只能有一个实例，并且绑定到一个扩展点.</p>
 *
 * @author NorthLan
 * @date 2021-02-24
 * @url https://noahlan.com
 */
@Deprecated
public interface IExtPolicy {
    /**
     * 根据 给定条件，定位匹配的扩展点.
     *
     * @param params 领域模型
     * @return 匹配的扩展点编码, SHOULD NEVER be null
     */
    @NotNull
    String extensionCode(@NotNull Object params);
}
