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

package org.lan.iti.common.model.page;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页查询参数封装
 *
 * @author NorthLan
 * @date 2020-04-10
 * @url https://noahlan.com
 */
@Data
@Accessors(chain = true)
@Validated
public class PageParameter implements Serializable {
    private static final long serialVersionUID = -8857063986322273205L;

    /**
     * 默认查询页数
     */
    public static final Long DEFAULT_PAGE_SIZE = 10L;

    /**
     * 默认查询当前页号
     */
    public static final Long DEFAULT_CURRENT = 1L;

    /**
     * 当前页号(1开始)
     */
    @NotNull
    @Min(value = 1)
    private Long current = DEFAULT_CURRENT;

    /**
     * 每页容量
     */
    @NotNull
    @Min(value = 1)
    private Long size = DEFAULT_PAGE_SIZE;

    /**
     * 排序字段信息
     */
    private List<OrderItem> orders = new ArrayList<>();

    /**
     * PageQuery是否为空
     * current 或 size 有一个为空则返回false
     */
    public boolean isNull() {
        return current == null || size == null;
    }

    /**
     * 检查数据是否合理
     * 标准为 current size 分别均需要大于0
     */
    public boolean isValid() {
        if (isNull()) {
            return false;
        }
        return current > 0 && size > 0;
    }
}
