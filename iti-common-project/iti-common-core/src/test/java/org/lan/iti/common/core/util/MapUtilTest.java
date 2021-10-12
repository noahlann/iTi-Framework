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

package org.lan.iti.common.core.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author NorthLan
 * @date 2021/10/11
 * @url https://blog.noahlan.com
 */
public class MapUtilTest {
    private static final Map<String, Object> MAP = new HashMap<>();

    static {
        Map<String, Object> bMap = new HashMap<>();
        bMap.put("b-a", "111");
        bMap.put("b-b", Collections.singletonMap("b-b-a", "bba"));

        Map<String, Object> cMap = new HashMap<>();
        cMap.put("c-a", "c");
        cMap.put("d", "sdfsdf");

        MAP.put("a", "a");
        MAP.put("b", bMap);
        MAP.put("c", cMap);
        MAP.put("d", null);
    }

    @Test
    public void test() {
        Map<String, Object> result = MapUtil.flatten(MAP);
        Assertions.assertTrue(result.containsKey("b-a") &&
                result.containsKey("b-b") &&
                result.containsKey("c-a")
        );

        result = MapUtil.flattenByKeys(MAP, "c");
        Assertions.assertTrue(!result.containsKey("b-a") &&
                !result.containsKey("b-b") &&
                result.containsKey("c-a") &&
                result.get("d") != null
        );
    }
}
