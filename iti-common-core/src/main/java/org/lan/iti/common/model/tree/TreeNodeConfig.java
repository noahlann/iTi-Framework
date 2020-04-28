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

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 树 配置文件
 *
 * @author NorthLan
 * @date 2020-04-28
 * @url https://noahlan.com
 */
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class TreeNodeConfig implements Serializable {
    private static final long serialVersionUID = -1383271649207295543L;
    /**
     * 默认属性配置对象
     */
    public static TreeNodeConfig DEFAULT_CONFIG = new TreeNodeConfig();

    // 属性名配置字段
    private String idKey = "id";
    private String parentIdKey = "parentId";
    private String weightKey = "weight";
    private String nameKey = "name";
    private String childrenKey = "children";
    // 可以配置递归深度 从0开始计算 默认此配置为空,即不限制
    private Integer deep;
}
