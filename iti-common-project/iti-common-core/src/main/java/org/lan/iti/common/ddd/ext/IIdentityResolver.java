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

package org.lan.iti.common.ddd.ext;

import javax.validation.constraints.NotNull;

/**
 * 业务身份解析器.
 *
 * @author NorthLan
 * @date 2021-02-24
 * @url https://noahlan.com
 */
public interface IIdentityResolver extends IPlugable {

    /**
     * 根据 所给参数 判断是否属于本业务身份的业务
     *
     * @param params 参数
     * @return true if yes
     */
    boolean match(@NotNull Object params);
}