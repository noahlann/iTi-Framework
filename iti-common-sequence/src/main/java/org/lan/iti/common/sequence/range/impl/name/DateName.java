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

import cn.hutool.core.date.DateUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.lan.iti.common.sequence.range.Name;

/**
 * 根据时间重置name
 *
 * @author NorthLan
 * @date 2020-05-06
 * @url https://noahlan.com
 */
@NoArgsConstructor
@AllArgsConstructor
public class DateName implements Name {
    private String name;

    @Override
    public String create() {
        return this.name + DateUtil.today();
    }
}
