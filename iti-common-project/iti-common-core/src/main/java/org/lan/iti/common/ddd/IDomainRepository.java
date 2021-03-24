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

package org.lan.iti.common.ddd;

import org.lan.iti.common.ddd.model.IDomain;

import java.util.List;
import java.util.Optional;

/**
 * 顶层 聚合根 仓储接口
 *
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
public interface IDomainRepository<A extends IDomain> {
    /**
     * 通过ID获取聚合根
     *
     * @param id id
     * @return 可选聚合根
     */
    Optional<A> getById(String id);

    /**
     * 通过IDs获取聚合根列表
     *
     * @param ids ids
     * @return 聚合根列表
     */
    List<A> getAllById(Iterable<String> ids);

    /**
     * 持久化聚合根
     *
     * @param entity 聚合根实体
     * @return 回写过ID的聚合根对象
     */
    A save(A entity);

    /**
     * 更新聚合根
     *
     * @param entity 聚合根实体
     * @return 更新后的聚合根
     */
    default A update(A entity) {
        return save(entity);
    }

    /**
     * 删除聚合根
     *
     * @param entity 聚合根实体
     */
    void remove(A entity);

    /**
     * 通过ID删除聚合根
     *
     * @param id 聚合根ID
     */
    void removeById(String id);

    /**
     * 聚合根是否存在
     *
     * @param id 聚合根ID
     * @return true 存在
     */
    boolean exists(String id);
}
