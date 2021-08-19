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

package org.lan.iti.iha.simple;

import cn.hutool.core.lang.Assert;
import lombok.Data;
import lombok.experimental.UtilityClass;

/**
 * @author NorthLan
 * @date 2021/8/6
 * @url https://blog.noahlan.com
 */
@UtilityClass
@Data
public class IhaSimple {
    private static IhaSimpleContext context = IhaSimpleContext.builder().build();

    public void init(IhaSimpleContext context) {
        Assert.notNull(context);
        IhaSimple.context = context;
    }

    public IhaSimpleContext getContext() {
        return context;
    }

    public SimpleConfig getConfig() {
        return getContext().getConfig();
    }
}
