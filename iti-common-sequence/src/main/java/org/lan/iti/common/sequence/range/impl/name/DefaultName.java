/*
 *
 *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.common.sequence.range.impl.name;

import lombok.AllArgsConstructor;
import org.lan.iti.common.sequence.range.Name;

/**
 * 默认名称获取器
 * 根据传入名称返回name
 *
 * @author NorthLan
 * @date 2020-05-06
 * @url https://noahlan.com
 */
@AllArgsConstructor
public class DefaultName implements Name {
    private String name;

    @Override
    public String create() {
        return this.name;
    }
}
