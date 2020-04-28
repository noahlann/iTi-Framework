/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.common.model.tree;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * 树形节点 属性可通过 {@link TreeNodeConfig} 中被命名
 * 在你的项目里它可以是部门实体、地区实体等任意类树节点实体
 * 类树节点实体: 包含key，父Key
 * 还可以包含扩展字段
 *
 * @author NorthLan
 * @date 2020-02-20
 * @url https://noahlan.com
 */
@NoArgsConstructor
public class TreeNode<T> implements Node<T> {

    /**
     * 当前节点ID
     */
    private T id;

    /**
     * 父级节点ID
     */
    private T parentId;

    /**
     * 名称
     */
    private CharSequence name;

    /**
     * 顺序 越小优先级越高 默认0
     */
    private Comparable<?> weight = 0;

    /**
     * 扩展字段
     */
    @Getter
    @Setter
    private Map<String, Object> extra;

    /**
     * 构造
     *
     * @param id       ID
     * @param parentId 父ID
     * @param name     名称
     * @param weight   权重
     */
    public TreeNode(T id, T parentId, String name, Comparable<?> weight) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        if (weight != null) {
            this.weight = weight;
        }
    }

    @Override
    public T getId() {
        return this.id;
    }

    @Override
    public TreeNode<T> setId(T id) {
        this.id = id;
        return this;
    }

    @Override
    public T getParentId() {
        return this.parentId;
    }

    @Override
    public TreeNode<T> setParentId(T parentId) {
        this.parentId = parentId;
        return this;
    }

    @Override
    public CharSequence getName() {
        return this.name;
    }

    @Override
    public TreeNode<T> setName(CharSequence name) {
        this.name = name;
        return this;
    }

    @Override
    public Comparable<?> getWeight() {
        return this.weight;
    }

    @Override
    public TreeNode<T> setWeight(Comparable<?> weight) {
        this.weight = weight;
        return this;
    }
}
