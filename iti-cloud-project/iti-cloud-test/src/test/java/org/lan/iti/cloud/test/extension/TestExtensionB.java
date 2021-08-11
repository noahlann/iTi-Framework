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

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author NorthLan
 * @date 2021/8/4
 * @url https://blog.noahlan.com
 */
@Component
@AllArgsConstructor
public class TestExtensionB implements TestExtensionInterface {
    private final TestExtensionA testExtensionA;

    @Override
    public boolean matches(String params) {
        return "B".equals(params);
    }

    @Override
    public void println() {
        System.out.println("B");
        testExtensionA.println();
    }
}
