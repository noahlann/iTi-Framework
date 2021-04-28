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

package org.lan.iti.common.core.util.idgen.snowflake;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Id生成时回调参数
 *
 * @author NorthLan
 * @date 2021-04-27
 * @url https://noahlan.com
 */
@Data
@AllArgsConstructor
public class OverCostActionArg {
    /**
     * 事件类型
     * 1-开始，2-结束，8-漂移
     */
    private int actionType = 0;

    /**
     * 时间戳
     */
    private long timeTick = 0;

    /**
     * 机器码
     */
    private short workerId = 0;

    /**
     *
     */
    private int overCostCountInOneTerm = 0;

    /**
     * 漂移期间生产ID个数
     */
    private int genCountInOneTerm = 0;

    /**
     * 漂移周期
     */
    private int termIndex = 0;
}
