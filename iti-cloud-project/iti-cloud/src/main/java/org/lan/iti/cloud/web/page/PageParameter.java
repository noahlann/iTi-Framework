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

package org.lan.iti.cloud.web.page;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 统一分页参数,使用不同适配器适配
 *
 * @author NorthLan
 * @date 2021-07-21
 * @url https://noahlan.com
 * @see org.lan.iti.cloud.jpa.JpaPageHelper
 * @deprecated 暂时弃用，使用jpa的pageable替代
 */
@Data
@ApiModel("分页参数")
@Deprecated
public class PageParameter {

    @ApiModelProperty("页码，从0开始（具体看配置）")
    private Integer page = 0;

    @ApiModelProperty("每页条目数量，默认10（具体看配置）")
    private Integer size = 10;

    @ApiModelProperty("排序，格式 property(#asc|desc) 不填排序方式默认为asc升序；示例：?sort=a&sort=b#asc&sort=c#desc")
    private List<String> sort = new ArrayList<>();
}
