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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 树形节点
 *
 * @author NorthLan
 * @date 2020-02-20
 * @url https://noahlan.com
 */
@Data
@ApiModel("树形节点")
public class TreeNode implements Serializable {
    private static final long serialVersionUID = 2230740354879863726L;

    /**
     * 当前节点ID
     */
    @ApiModelProperty("当前节点")
    private Long id = null;

    /**
     * 父级节点ID
     */
    @ApiModelProperty("父级节点")
    private Long parentId = null;

    /**
     * 子节点列表
     */
    @ApiModelProperty("子节点列表")
    @Setter(AccessLevel.PRIVATE)
    private List<TreeNode> children = new ArrayList<>();

    /**
     * 排序值
     */
    @ApiModelProperty("排序值")
    private Integer sort = 0;

    // region 方法列表

    /**
     * 添加子节点
     */
    public void addChild(TreeNode node) {
        children.add(node);
    }

    /**
     * 递归排序
     * <p>
     * 建议仅调用根节点进行排序
     *
     * @param comparator 比较器,若为空值则默认按照sort降序排序
     */
    public void sortChildren(Comparator<? super TreeNode> comparator) {
        children.sort(comparator != null ? comparator : Comparator.comparingInt(TreeNode::getSort));
        // 所有子节点排个序
        children.forEach(it -> it.sortChildren(comparator));
    }
    // endregion
}
