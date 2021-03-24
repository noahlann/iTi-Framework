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

package org.lan.iti.common.ddd.model;

/**
 * 实体，对应DDD实体
 * <p>
 * <p>可以简单理解为，具有唯一标识的对象称为实体</p>
 *
 * @author NorthLan
 * @date 2021-02-01
 * @url https://noahlan.com
 */
public interface IEntity {
    /**
     * 获取实体唯一标识
     *
     * @return 唯一标识
     */
    String getId();

    /**
     * 提供外部设定ID的能力
     * <p>注意：提供公开的外部Setter是为了性能与便捷性。</p>
     * <p>谨防架构腐化，请严格使用架构守护者。</p>
     *
     * @param id 唯一标识
     */
    void setId(String id);
}
