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

package org.lan.iti.common.core.util;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 * @author NorthLan
 * @date 2020-03-16
 * @url https://noahlan.com
 */
public class FormatterTest {

    @Test
    public void test() {
        String aaa = Formatter.format("test: {}", 2333);
        String bbb = Formatter.format("test: {}", "字符串");
        Assert.assertEquals(aaa, "test: 2333");
        Assert.assertEquals(bbb, "test: 字符串");
    }
}
