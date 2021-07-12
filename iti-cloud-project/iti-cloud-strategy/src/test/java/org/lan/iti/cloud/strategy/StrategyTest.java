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

package org.lan.iti.cloud.strategy;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.lan.iti.common.extension.ExtensionLoader;
import org.lan.iti.common.extension.adapter.ExtensionAdapter;
import org.lan.iti.common.extension.adapter.parameter.ExtensionAdapterParameter;
import org.reflections.Reflections;

/**
 * @author NorthLan
 * @date 2021-07-10
 * @url https://noahlan.com
 */
public class StrategyTest {
    @Test
    public void test() {
        ExtensionAdapterParameter parameter = ExtensionAdapterParameter.builder()
                .reflect()
                .packageNames("org.lan.iti.cloud")
                .interfaceClass(IStrategy.class)
                .checkInterfaceClass(true)
                .build();
        ExtensionLoader.getLoader(ExtensionAdapter.class).getExtension(parameter).init();
    }

    @Test
    public void testReflections() {
        Reflections reflection = new Reflections("org.lan.iti.cloud.strategy");

        val types = reflection.getSubTypesOf(IStrategy.class);

        System.out.println(types);
    }
}
