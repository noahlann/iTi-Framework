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

/**
 * 生成器接口
 *
 * @author NorthLan
 * @date 2021-04-27
 * @url https://noahlan.com
 */
public interface IGenerator {
    /**
     * 生成ID
     *
     * @return 生成下一个ID
     * @throws IdGeneratorException 异常信息
     */
    long nextLong() throws IdGeneratorException;

    /**
     * 生成String类型的ID{@code String.format(format, nextLong())}
     *
     * @param format 格式化串
     * @return String类型的ID
     * @throws IdGeneratorException 异常信息
     */
    String nextStr(String format) throws IdGeneratorException;

    /**
     * 生成String类型的ID{@code String.valueOf(nextLong())}
     *
     * @return String类型的ID
     * @throws IdGeneratorException 异常信息
     */
    String nextStr() throws IdGeneratorException;
}
