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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Getter;

/**
 * 动态表名条件封装器
 *
 * @author NorthLan
 * @date 2020-07-02
 * @url https://noahlan.com
 */
public class LambdaDynamicQueryWrapper<T> extends LambdaQueryWrapper<T> {
    private static final long serialVersionUID = -1068655342484783766L;

    @Getter
    private String tablePrefix;

    @Getter
    private String tableSuffix;

    public LambdaDynamicQueryWrapper() {
        super();
    }

    public LambdaDynamicQueryWrapper(T entity) {
        super(entity);
    }

    public LambdaDynamicQueryWrapper(Class<T> entityClass) {
        super(entityClass);
    }

    /**
     * 设置动态表名前缀
     *
     * @param tablePrefix 动态表名前缀
     * @return 条件构造器
     */
    public LambdaDynamicQueryWrapper<T> tablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
        return this;
    }

    /**
     * 设置动态表名后缀
     *
     * @param tableSuffix 动态表名后缀
     * @return 条件构造器
     */
    public LambdaDynamicQueryWrapper<T> tableSuffix(String tableSuffix) {
        this.tableSuffix = tableSuffix;
        return this;
    }
}
