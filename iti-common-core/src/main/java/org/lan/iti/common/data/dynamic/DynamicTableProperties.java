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

package org.lan.iti.common.data.dynamic;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态表名配置
 *
 * @author NorthLan
 * @date 2020-07-02
 * @url https://noahlan.com
 */
@Data
@ConfigurationProperties(prefix = DynamicTableProperties.PREFIX)
public class DynamicTableProperties {
    public static final String PREFIX = "iti.dynamic.table-name";

    /**
     * 是否启用
     */
    private boolean enabled = false;

    /**
     * 需要执行动态表名的表名列表
     */
    private List<TableNameDict> names = new ArrayList<>();

    /**
     * 表名字典
     */
    @Data
    public static class TableNameDict {
        /**
         * 完整表名
         */
        private String fullName;

        /**
         * 干净表名
         */
        private String name;
    }
}
