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

package org.lan.iti.common.extension;

import org.lan.iti.common.core.support.Ordered;

/**
 * @author NorthLan
 * @date 2021-07-08
 * @url https://noahlan.com
 */
public interface IExtension<T> extends Ordered {

    /**
     * 是否匹配
     *
     * @param params 参数
     * @return true or false
     */
    boolean matches(T params);

    @Override
    default int getOrder() {
        return Integer.MAX_VALUE;
    }
}
