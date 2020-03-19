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

package org.lan.iti.common.security.social.connect;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author NorthLan
 * @date 2020-03-18
 * @url https://noahlan.com
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NullApiAdapter implements ApiAdapter<Object> {

    public final static NullApiAdapter INSTANCE = new NullApiAdapter();

    @Override
    public boolean test(Object api) {
        return true;
    }

    @Override
    public void setConnectionValues(Object api, ConnectionValues values) {

    }

    @Override
    public void updateStatus(Object api, String message) {

    }
}
