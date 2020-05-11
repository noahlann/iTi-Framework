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
import org.lan.iti.common.scanner.model.ResourceDefinition;

import java.util.Arrays;
import java.util.List;

/**
 * @author NorthLan
 * @date 2020-05-11
 * @url https://noahlan.com
 */
public class ResourceCacheTest {

    @Test
    public void testSort() {
        ResourceCache.register(Arrays.asList(
                new ResourceDefinition()
                        .setUrl("/user/current")
                        .setCode("current")
                        .setHttpMethod("GET")
                        .setName("current")
                        .setModuleCode("user")
                        .setModuleName("user")
                        .setServiceCode("iti-upms")
                        .setServiceName("haha")
                , new ResourceDefinition()
                        .setUrl("/menu/tree")
                        .setCode("menu")
                        .setHttpMethod("GET")
                        .setName("current")
                        .setModuleCode("user")
                        .setModuleName("user")
                        .setServiceCode("iti-upms")
                        .setServiceName("haha")
                , new ResourceDefinition()
                        .setUrl("/user/{id}")
                        .setCode("get")
                        .setHttpMethod("GET")
                        .setName("get")
                        .setModuleCode("user")
                        .setModuleName("user")
                        .setServiceCode("iti-upms")
                        .setServiceName("haha")
        ));
        ResourceCache.sort();
        boolean exp = true;
        String[] expArr = new String[]{"/user/current", "/menu/tree", "/user/{id}"};
        List<ResourceDefinition> all = ResourceCache.getAllResources();
        for (int i = 0; i < all.size(); i++) {
            if (!expArr[i].equals(all.get(i).getUrl())) {
                exp = false;
                break;
            }
        }
        Assert.assertTrue(exp);
    }
}
