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

package org.lan.iti.cloud.test.extension;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.lan.iti.common.extension.ExtensionLoader;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author NorthLan
 * @date 2021/8/4
 * @url https://blog.noahlan.com
 */
//@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ExtensionTest {

    @Test
    public void test() {
        val loader = ExtensionLoader.getLoader(TestExtensionInterface.class);
        loader.getFirst("A").println();
        loader.getFirst("B").println();
        loader.getFirst("C").println();
    }
}
