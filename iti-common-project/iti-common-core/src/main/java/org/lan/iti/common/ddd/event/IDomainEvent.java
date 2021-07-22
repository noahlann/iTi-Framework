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

package org.lan.iti.common.ddd.event;

import java.time.LocalDateTime;

/**
 * 领域事件接口
 *
 * @author NorthLan
 * @date 2021-01-26
 * @url https://noahlan.com
 */
@Deprecated
public interface IDomainEvent {
    /**
     * 获取事件版本号
     *
     * @return 事件版本号
     */
    int getEventVersion();

    /**
     * 事件发生时间
     *
     * @return 发生时间
     */
    LocalDateTime getOccuredOn();
}
