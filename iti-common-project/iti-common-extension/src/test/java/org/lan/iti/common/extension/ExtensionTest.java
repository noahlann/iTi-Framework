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

package org.lan.iti.common.extension;

import lombok.var;
import org.junit.jupiter.api.Test;
import org.lan.iti.common.core.util.ClassLoaderUtil;
import org.lan.iti.common.extension.adapter.ExtensionAdapter;
import org.lan.iti.common.extension.adapter.parameter.ExtensionAdapterParameter;

/**
 * @author NorthLan
 * @date 2021-07-08
 * @url https://noahlan.com
 */
public class ExtensionTest {
    @Test
    public void test() {
        long nano = System.nanoTime();

        ExtensionAdapterParameter param = ExtensionAdapterParameter.builder()
                .reflect()
                .classLoader(ClassLoaderUtil.getClassLoader(ExtensionTest.class))
                .packageNames("org.lan.iti.common.extension")
                .build();
        ExtensionLoader.getAdapterFactory().addParameter(param).init();

        System.out.println("init cost: " + (System.nanoTime() - nano));
        nano = System.nanoTime();

        var loader = ExtensionLoader.getLoader(ITestExtensionA.class);
        var extension = loader.getFirst("TestExtensionA_A");
        System.out.println("result: " + extension.test());
        System.out.println("1 cost: " + (System.nanoTime() - nano));

        nano = System.nanoTime();
        loader = ExtensionLoader.getLoader(ITestExtensionA.class);
        extension = loader.getFirst("TestExtensionA_A");
        System.out.println("result: " + extension.test());
        System.out.println("2 cost: " + (System.nanoTime() - nano));

        nano = System.nanoTime();
        loader = ExtensionLoader.getLoader(ITestExtensionA.class);
        extension = loader.getFirst("ab", true);
        System.out.println("result: " + extension.test());
        System.out.println("3 cost: " + (System.nanoTime() - nano));

        nano = System.nanoTime();
        loader = ExtensionLoader.getLoader(ITestExtensionA.class);
        extension = loader.getFirst("ab", true);
        System.out.println("result: " + extension.test());
        System.out.println("4 cost: " + (System.nanoTime() - nano));
    }

    @Test
    public void testSPI() {
        long nano = System.nanoTime();
        var loader = ExtensionLoader.getLoader(ITestExtensionA.class);
        var extension = loader.getFirst("TestExtensionA_A");
        System.out.println("result: " + extension.test());
        System.out.println("1 cost: " + (System.nanoTime() - nano));

        nano = System.nanoTime();
        loader = ExtensionLoader.getLoader(ITestExtensionA.class);
        extension = loader.getFirst("TestExtensionA_B");
        System.out.println("result: " + extension.test());
        System.out.println("2 cost: " + (System.nanoTime() - nano));

        nano = System.nanoTime();
        loader = ExtensionLoader.getLoader(ITestExtensionA.class);
        extension = loader.getFirst("testExtensionA_B", true);
        System.out.println("result: " + extension.test());
        System.out.println("3 cost: " + (System.nanoTime() - nano));

        nano = System.nanoTime();
        loader = ExtensionLoader.getLoader(ITestExtensionA.class);
        extension = loader.getFirst("ab", true);
        System.out.println("result: " + extension.test());
        System.out.println("4 cost: " + (System.nanoTime() - nano));
    }
}
