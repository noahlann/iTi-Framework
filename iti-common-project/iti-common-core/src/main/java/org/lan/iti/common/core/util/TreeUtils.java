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

package org.lan.iti.common.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.experimental.UtilityClass;
import org.lan.iti.common.core.tree.Tree;
import org.lan.iti.common.core.tree.TreeNode;
import org.lan.iti.common.core.tree.TreeNodeConfig;
import org.lan.iti.common.core.tree.parser.DefaultNodeParser;
import org.lan.iti.common.core.tree.parser.NodeParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 树结构 工具
 *
 * @author NorthLan
 * @date 2020-04-28
 * @url https://noahlan.com
 */
@UtilityClass
public class TreeUtils {

    // region build
    /**
     * 树构建
     *
     * @param list 源数据集合
     * @return List
     */
    public List<Tree<Integer>> build(List<TreeNode<Integer>> list) {
        return build(list, -1);
    }

    /**
     * 树构建
     *
     * @param <E>      ID类型
     * @param list     源数据集合
     * @param parentId 最顶层父id值 一般为 0 之类
     * @return List
     */
    public <E> List<Tree<E>> build(List<TreeNode<E>> list, E parentId) {
        return build(list, parentId, TreeNodeConfig.DEFAULT_CONFIG, new DefaultNodeParser<>());
    }

    /**
     * 树构建
     *
     * @param <T>        转换的实体 为数据源里的对象类型
     * @param <E>        ID类型
     * @param list       源数据集合
     * @param parentId   最顶层父id值 一般为 0 之类
     * @param nodeParser 转换器
     * @return List
     */
    public <T, E> List<Tree<E>> build(List<T> list, E parentId, NodeParser<T, E> nodeParser) {
        return build(list, parentId, TreeNodeConfig.DEFAULT_CONFIG, nodeParser);
    }

    /**
     * 树构建（递归）
     *
     * @param list           源数据集合
     * @param parentId       最顶层父id值 一般为 0 之类
     * @param treeNodeConfig 配置
     * @param nodeParser     转换器
     * @param <T>            转换的实体 为数据源里的对象类型
     * @param <E>            ID类型
     * @return 构建完成的树节点列表
     */
    public <T, E> List<Tree<E>> build(List<T> list, E parentId, TreeNodeConfig treeNodeConfig, NodeParser<T, E> nodeParser) {
        final List<Tree<E>> treeList = new ArrayList<>();
        Tree<E> tree;
        for (T obj : list) {
            tree = new Tree<>(treeNodeConfig);
            nodeParser.parse(obj, tree);
            treeList.add(tree);
        }

        List<Tree<E>> finalTreeList = new ArrayList<>();
        for (Tree<E> node : treeList) {
            if (parentId.equals(node.getParentId())) {
                finalTreeList.add(node);
                innerBuild(treeList, node, 0, treeNodeConfig.getDeep());
            }
        }

        // 内存每层已经排过了 这是最外层排序
        finalTreeList = finalTreeList.stream().sorted().collect(Collectors.toList());
        return finalTreeList;
    }

    /**
     * 递归处理
     *
     * @param treeNodes  数据集合
     * @param parentNode 当前节点
     * @param deep       已递归深度
     * @param maxDeep    最大递归深度 可能为null即不限制
     */
    private <T> void innerBuild(List<Tree<T>> treeNodes, Tree<T> parentNode, int deep, Integer maxDeep) {
        if (CollUtil.isEmpty(treeNodes)) {
            return;
        }
        if (maxDeep != null && deep >= maxDeep) {
            return;
        }
        // 每层排序 TreeNodeMap 实现了Comparable接口
        treeNodes = treeNodes.stream().sorted().collect(Collectors.toList());
        for (Tree<T> childNode : treeNodes) {
            if (parentNode.getId().equals(childNode.getParentId())) {
                List<Tree<T>> children = parentNode.getChildren();
                if (children == null) {
                    children = new ArrayList<>();
                    parentNode.setChildren(children);
                }
                children.add(childNode);
                childNode.setParent(parentNode);
                innerBuild(treeNodes, childNode, deep + 1, maxDeep);
            }
        }
    }
    // endregion

    // region getter
    /**
     * 获取ID对应的节点，如果有多个ID相同的节点，只返回第一个。<br>
     * 此方法只查找此节点及子节点，采用广度优先遍历。
     *
     * @param <T> ID类型
     * @param node 节点
     * @param id ID
     * @return 节点
     */
    public static <T> Tree<T> getNode(Tree<T> node, T id) {
        if (ObjectUtil.equal(id, node.getId())) {
            return node;
        }

        // 查找子节点
        Tree<T> childNode;
        for (Tree<T> child : node.getChildren()) {
            childNode = child.getNode(id);
            if (null != childNode) {
                return childNode;
            }
        }

        // 未找到节点
        return null;
    }

    /**
     * 获取所有父节点名称列表
     *
     * <p>
     * 比如有个人在研发1部，他上面有研发部，接着上面有技术中心<br>
     * 返回结果就是：[研发一部, 研发中心, 技术中心]
     *
     * @param <T> 节点ID类型
     * @param node 节点
     * @param includeCurrentNode 是否包含当前节点的名称
     * @return 所有父节点名称列表，node为null返回空List
     */
    public static <T> List<CharSequence> getParentsName(Tree<T> node, boolean includeCurrentNode) {
        final List<CharSequence> result = new ArrayList<>();
        if(null == node){
            return result;
        }

        if (includeCurrentNode) {
            result.add(node.getName());
        }

        Tree<T> parent = node.getParent();
        while (null != parent) {
            result.add(parent.getName());
            parent = parent.getParent();
        }
        return result;
    }
    // endregion
}
