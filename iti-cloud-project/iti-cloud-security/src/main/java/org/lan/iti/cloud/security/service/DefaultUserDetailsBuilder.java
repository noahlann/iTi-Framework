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

package org.lan.iti.cloud.security.service;

import cn.hutool.core.bean.BeanUtil;
import org.lan.iti.cloud.security.model.ITIUserDetails;

/**
 * 默认的用户构建器
 *
 * @author NorthLan
 * @date 2020-04-18
 * @url https://noahlan.com
 */
public class DefaultUserDetailsBuilder extends AbstractUserDetailsBuilder {

    @Override
    protected ITIUserDetails build(Object user, String providerId, String domain) {
        return BeanUtil.toBean(user, ITIUserDetails.class);
    }
}