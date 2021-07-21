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

package org.lan.iti.iha.server.pipeline;

import org.lan.iti.iha.server.model.UserDetails;
import org.lan.iti.common.extension.IExtension;
import org.lan.iti.common.extension.annotation.Extension;

/**
 * Pipeline for sign in flow
 *
 * @author NorthLan
 * @date 2021-07-06
 * @url https://noahlan.com
 */
@Extension
public interface SignInPipeline extends Pipeline<UserDetails>, IExtension<Object> {
    @Override
    default boolean matches(Object params) {
        return true;
    }
}
