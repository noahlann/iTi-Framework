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

package org.lan.iti.common.core.tree.parser;

import cn.hutool.core.map.MapUtil;
import org.lan.iti.common.core.tree.Tree;
import org.lan.iti.common.core.tree.TreeNode;

import java.util.Map;

/**
 * 默认的简单解析器
 *
 * @author NorthLan
 * @date 2020-04-28
 * @url https://noahlan.com
 */
public class DefaultNodeParser<T> implements NodeParser<TreeNode<T>, T> {
    @Override
    public void parse(TreeNode<T> source, Tree<T> tree) {
        tree.setId(source.getId());
        tree.setParentId(source.getParentId());
        tree.setWeight(source.getWeight());
        tree.setName(source.getName());

        // 扩展字段
        final Map<String, Object> extra = source.getExtra();
        if (MapUtil.isNotEmpty(extra)) {
            extra.forEach(tree::putExtra);
        }
    }
}
