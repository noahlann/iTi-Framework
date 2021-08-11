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

package org.lan.iti.cloud.extension;

import org.lan.iti.common.extension.adapter.parameter.AbstractParameterBuilder;
import org.lan.iti.common.extension.adapter.parameter.ParameterBuilder;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * @author NorthLan
 * @date 2021-07-11
 * @url https://noahlan.com
 */
public class SpringExtensionParameterBuilder extends AbstractParameterBuilder {
    public SpringExtensionParameterBuilder(ParameterBuilder builder, Map<String, Object> map) {
        super(builder, map);
    }

    public SpringExtensionParameterBuilder applicationContext(ApplicationContext applicationContext) {
        map.put(SpringExtensionConstants.KEY_APPLICATION_CONTEXT, applicationContext);
        return this;
    }
}
