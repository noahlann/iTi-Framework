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

package org.lan.iti.common.core.tree;


import javax.validation.constraints.NotNull;

/**
 * 节点接口
 *
 * @author NorthLan
 * @date 2020-04-28
 * @url https://noahlan.com
 */
public interface Node<T> extends Comparable<Node<T>> {
    /**
     * 获取ID
     *
     * @return id
     */
    T getId();

    /**
     * 设置ID
     *
     * @param id ID
     * @return this
     */
    Node<T> setId(T id);

    /**
     * 获取父节点ID
     *
     * @return 父节点ID
     */
    T getParentId();

    /**
     * 设置父节点ID
     *
     * @param parentId 父节点ID
     * @return this
     */
    Node<T> setParentId(T parentId);

    /**
     * 获取节点标签名称
     *
     * @return 节点标签名称
     */
    CharSequence getName();

    /**
     * 设置节点标签名称
     *
     * @param name 节点标签名称
     * @return this
     */
    Node<T> setName(CharSequence name);

    /**
     * 获取权重
     *
     * @return 权重
     */
    Comparable<?> getWeight();

    /**
     * 设置权重
     *
     * @param weight 权重
     * @return this
     */
    Node<T> setWeight(Comparable<?> weight);

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    default int compareTo(@NotNull Node<T> o) {
        final Comparable weight = this.getWeight();
        if (null != weight) {
            final Comparable weightOther = o.getWeight();
            return weight.compareTo(weightOther);
        }
        return 0;
    }
}
