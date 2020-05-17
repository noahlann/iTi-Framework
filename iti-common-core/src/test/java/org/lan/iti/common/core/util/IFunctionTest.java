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

import lombok.Data;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.lan.iti.common.core.interfaces.IFunction;

/**
 * @author NorthLan
 * @date 2020-04-24
 * @url https://noahlan.com
 */
public class IFunctionTest {
    @Test
    public void test() {
        Niu niu = new Niu();
        String result = fieldName(niu::test);
        Assert.assertEquals("test", result);
        result = fieldName((IFunction<Niu, ?>) Niu::getClazz);
        Assert.assertEquals("clazz", result);
    }

    @Data
    static class Niu {
        private String clazz;

        public Long test(Integer a) {
            return 1L;
        }
    }

    private <T> String fieldName(IFunction<T, ?> function) {
        return function.getImplFieldName();
    }
}
