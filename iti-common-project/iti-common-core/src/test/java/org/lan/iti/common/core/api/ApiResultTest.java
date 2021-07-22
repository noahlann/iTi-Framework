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

package org.lan.iti.common.core.api;

import com.xkcoding.json.JsonUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author NorthLan
 * @date 2021-07-22
 * @url https://noahlan.com
 */
public class ApiResultTest {

    @Test
    public void test() {
        ApiResult<String> test = ApiResult.ok("23332333");

        test.page(1);
        test.totalElements(2);

        Assertions.assertEquals("{\"code\":200,\"data\":\"23332333\",\"page\":1,\"message\":\"成功\",\"totalElements\":2}", JsonUtil.toJsonString(test));
    }
}
