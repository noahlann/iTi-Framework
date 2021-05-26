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

package org.lan.iti.cloud.axon.command;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * 抽象命令类
 *
 * @author NorthLan
 * @date 2021-04-28
 * @url https://noahlan.com
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@SuperBuilder
public abstract class AbstractCommand {

    @TargetAggregateIdentifier
    public String getId() {
        return identifier();
    }

    /**
     * 获取ID
     * <p>此ID作为标识聚合根唯一性必须</p>
     *
     * @return 唯一标识
     */
    protected abstract String identifier();

    /**
     * 是否具有唯一标识
     *
     * @return true 具有
     */
    public boolean hasIdentifier() {
        return StrUtil.isNotBlank(identifier());
    }
}
