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
public interface IDomainRepository<Domain extends IDomain> {
    /**
     * 通过ID获取聚合根
     *
     * @param id id
     * @return 可选聚合根
     */
    Optional<Domain> loadById(String id);

    /**
     * 根据聚合根ID从仓储中取出一批聚合根
     *
     * @param ids id列表
     * @return 若无值，则返回空列表
     */
    List<Domain> loadAllByIds(Iterable<String> ids);

    /**
     * 取出此仓储中的所有聚合根
     *
     * @return 若无值，则返回空列表
     */
    List<Domain> loadAll();

    /**
     * 仓储中聚合根的数量
     *
     * @return 数量
     */
    long size();

    /**
     * 往仓储中保存聚合根
     * <p>若聚合根存在，则更新</p>
     *
     * @param domain 聚合根实体
     */
    void save(Domain domain);

    /**
     * 删除仓储中的某个聚合根，此方法是为了方便
     *
     * @param domain 聚合根实体
     */
    void remove(Domain domain);

    /**
     * 领域驱动设计中，不存在删除聚合的理念，应当标记为禁用或启用
     *
     * @param domain 聚合根实体
     */
    void disable(Domain domain);

    /**
     * 仓储中是否包含聚合根(通过ID)
     *
     * @param domain 聚合根
     * @return true包含 false不包含
     */
    boolean contains(Domain domain);
}
