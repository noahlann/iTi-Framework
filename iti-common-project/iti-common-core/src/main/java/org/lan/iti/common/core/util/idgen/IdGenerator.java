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

package org.lan.iti.common.core.util.idgen;

import org.lan.iti.common.core.util.idgen.exception.IdGeneratorException;
import org.lan.iti.common.core.util.idgen.snowflake.SnowflakeOptions;

/**
 * @author NorthLan
 * @date 2021-04-27
 * @url https://noahlan.com
 */
public class IdGenerator {
    private static IGenerator INSTANCE = null;

    public static IGenerator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DefaultIdGenerator(new SnowflakeOptions());
        }
        return INSTANCE;
    }

    public static void setOptions(SnowflakeOptions options) {
        INSTANCE = new DefaultIdGenerator(options);
    }

    public static long nextLong() throws IdGeneratorException {
        return getInstance().nextLong();
    }

    public static String nextStr() throws IdGeneratorException {
        return getInstance().nextStr();
    }

    public static String nextStr(String format) throws IdGeneratorException {
        return getInstance().nextStr(format);
    }
}
