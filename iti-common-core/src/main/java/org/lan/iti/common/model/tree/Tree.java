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

package org.lan.iti.common.model.tree;


import cn.hutool.core.util.StrUtil;
import lombok.Getter;
import org.lan.iti.common.model.util.TreeUtils;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 通过转换器将你的实体转化为TreeNodeMap节点实体 属性都存在此处,属性有序，可支持排序
 *
 * @author NorthLan
 * @date 2020-04-28
 * @url https://noahlan.com
 */
@SuppressWarnings("unchecked")
public class Tree<T> extends LinkedHashMap<String, Object> implements Node<T> {
    private static final long serialVersionUID = -7919561791578571797L;

    private final TreeNodeConfig treeNodeConfig;

    @Getter
    private Tree<T> parent;

    public Tree() {
        this(null);
    }

    public Tree(TreeNodeConfig treeNodeConfig) {
        super();
        if (treeNodeConfig == null) {
            treeNodeConfig = TreeNodeConfig.DEFAULT_CONFIG;
        }
        this.treeNodeConfig = treeNodeConfig;
    }

    // region utils

    /**
     * 获取ID对应的节点，如果有多个ID相同的节点，只返回第一个。<br>
     * 此方法只查找此节点及子节点，采用广度优先遍历。
     *
     * @param id ID
     * @return 节点
     */
    public Tree<T> getNode(T id) {
        return TreeUtils.getNode(this, id);
    }

    /**
     * 获取所有父节点名称列表,从某节点开始
     *
     * <p>
     * 比如有个人在研发1部，他上面有研发部，接着上面有技术中心<br>
     * 返回结果就是：[研发一部, 研发中心, 技术中心]
     *
     * @param id                 节点ID
     * @param includeCurrentNode 是否包含当前节点的名称
     * @return 所有父节点名称列表
     */
    public List<CharSequence> getParentsName(T id, boolean includeCurrentNode) {
        return TreeUtils.getParentsName(getNode(id), includeCurrentNode);
    }

    /**
     * 获取所有父节点名称列表,从本节点开始
     *
     * <p>
     * 比如有个人在研发1部，他上面有研发部，接着上面有技术中心<br>
     * 返回结果就是：[研发一部, 研发中心, 技术中心]
     *
     * @param includeCurrentNode 是否包含当前节点的名称
     * @return 所有父节点名称列表
     */
    public List<CharSequence> getParentsName(boolean includeCurrentNode) {
        return TreeUtils.getParentsName(this, includeCurrentNode);
    }
    // endregion

    // region public

    /**
     * 设置父节点
     *
     * @param parent 父节点
     * @return this
     */
    public Tree<T> setParent(Tree<T> parent) {
        this.parent = parent;
        if (null != parent) {
            this.setParentId(parent.getId());
        }
        return this;
    }

    public List<Tree<T>> getChildren() {
        return (List<Tree<T>>) this.get(treeNodeConfig.getChildrenKey());
    }

    public Tree<T> setChildren(List<Tree<T>> children) {
        this.put(treeNodeConfig.getChildrenKey(), children);
        return this;
    }

    /**
     * 扩展属性
     *
     * @param key   键
     * @param value 扩展值
     */
    public Tree<T> putExtra(String key, Object value) {
        Assert.isTrue(StrUtil.isNotEmpty(key), "key must be not empty!");
        this.put(key, value);
        return this;
    }
    // endregion

    // region override
    @Override
    public T getId() {
        return (T) this.get(treeNodeConfig.getIdKey());
    }

    @Override
    public Tree<T> setId(T id) {
        this.put(treeNodeConfig.getIdKey(), id);
        return this;
    }

    @Override
    public T getParentId() {
        return (T) this.get(treeNodeConfig.getParentIdKey());
    }

    @Override
    public Tree<T> setParentId(T parentId) {
        this.put(treeNodeConfig.getParentIdKey(), parentId);
        return this;
    }

    @Override
    public CharSequence getName() {
        return (CharSequence) this.get(treeNodeConfig.getNameKey());
    }

    @Override
    public Tree<T> setName(CharSequence name) {
        this.put(treeNodeConfig.getNameKey(), name);
        return this;
    }

    @Override
    public Comparable<?> getWeight() {
        return (Comparable<?>) this.get(treeNodeConfig.getWeightKey());
    }

    @Override
    public Tree<T> setWeight(Comparable<?> weight) {
        this.put(treeNodeConfig.getWeightKey(), weight);
        return this;
    }
    // endregion
}
