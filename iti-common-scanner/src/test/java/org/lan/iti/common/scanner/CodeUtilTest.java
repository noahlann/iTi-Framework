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

package org.lan.iti.common.scanner;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.lan.iti.common.scanner.util.CodeUtils;

/**
 * @author NorthLan
 * @date 2020-04-22
 * @url https://noahlan.com
 */
public class CodeUtilTest {

    @Test
    public void convertCtrWildcardUrlTest() {
        /*
         * /test/ -> /test/**
         * /test -> /test/**"
         * /test/* -> /test/**" 且给予警告提示
         * /test/** -> /test/**
         * /test/* /xx -> /test/* /xx/**
         * /test/xx* -> null 不允许此类url存在
         * /test/*** -> null 不允许
         * test/ -> /test/**
         * test -> /test/**
         */
        //------------------  /test/**
        String excepted = "/test/**";
        String result = CodeUtils.convertCtrWildcardUrl("/test/");
        Assert.assertEquals(excepted, result);

        result = CodeUtils.convertCtrWildcardUrl("/test");
        Assert.assertEquals(excepted, result);

        result = CodeUtils.convertCtrWildcardUrl("/test/*");
        Assert.assertEquals(excepted, result);

        result = CodeUtils.convertCtrWildcardUrl("/test/**");
        Assert.assertEquals(excepted, result);

        result = CodeUtils.convertCtrWildcardUrl("test/");
        Assert.assertEquals(excepted, result);

        result = CodeUtils.convertCtrWildcardUrl("test");
        Assert.assertEquals(excepted, result);

        //------------------ null

        result = CodeUtils.convertCtrWildcardUrl("/test/xx*");
        Assert.assertNull(result);

        result = CodeUtils.convertCtrWildcardUrl("/test/***");
        Assert.assertNull(result);

        result = CodeUtils.convertCtrWildcardUrl("/test/*/xx");
        Assert.assertEquals("/test/*/xx/**", result);
    }

    @Test
    public void getResourceCodeTest() {
        String result = CodeUtils.getResourceCode("$", "iti-upms", "user", null);
        Assert.assertEquals("iti-upms$user", result);

        result = CodeUtils.getResourceCode("$", "iti-upms", "user", "getUser");
        Assert.assertEquals("iti-upms$user$getUser", result);
    }
}
