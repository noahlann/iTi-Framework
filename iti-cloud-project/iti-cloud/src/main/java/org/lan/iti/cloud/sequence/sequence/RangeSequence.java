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

package org.lan.iti.cloud.sequence.sequence;

import org.lan.iti.cloud.sequence.range.Name;
import org.lan.iti.cloud.sequence.range.RangeManager;

/**
 * 区间生成器接口
 *
 * @author NorthLan
 * @date 2020-05-06
 * @url https://noahlan.com
 */
public interface RangeSequence extends Sequence {
    /**
     * 设置区间管理器
     *
     * @param rangeManager 区间管理器
     */
    void setRangeManager(RangeManager rangeManager);

    /**
     * 设置获取序列号名称
     *
     * @param name 名称
     */
    void setName(Name name);
}
