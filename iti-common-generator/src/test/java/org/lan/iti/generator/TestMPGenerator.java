/*
 * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lan.iti.generator;


import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.lan.iti.generator.util.Resources;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author NorthLan
 * @date 2020-03-19
 * @url https://noahlan.com
 */
public class TestMPGenerator {

    @Test
    public void testProperties() {
        MPGenerator.run();
//        MPGenerator mpg = new MPGenerator();
//        mpg.setCfg(new InjectionConfig() {
//            @Override
//            public void initMap() {
//
//            }
//        }).execute();
        System.out.println(2333);
    }

    @Test
    @SneakyThrows
    public void testPath(){
        File file = new File("");
        String ab = file.getAbsolutePath();
        String ac = file.getCanonicalPath();
        String p = file.getParent();

        String d = System.getProperty("user.dir");
        String e = System.getProperty("project.dir");

        String f = getClass().getResource("").getPath();

        Path path = Paths.get("");
        Boolean a = path.isAbsolute();

        String currentDir = System.getProperty("user.dir");
        if (!Paths.get("").isAbsolute()) {
            // 相对路径,从根目录相对,获取根目录信息
            currentDir = currentDir.substring(0, currentDir.lastIndexOf('\\'));
        }

        System.out.println(23);
    }
}
