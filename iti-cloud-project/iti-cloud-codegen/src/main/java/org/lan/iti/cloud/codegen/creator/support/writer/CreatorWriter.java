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

package org.lan.iti.cloud.codegen.creator.support.writer;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.lan.iti.cloud.codegen.creator.support.meta.CreatorSetterMeta;

import java.util.List;

/**
 * Creator代码写入器接口
 *
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
public interface CreatorWriter {
    /**
     * 写入方法
     *
     * @param builder             类型写入
     * @param acceptMethodBuilder accept方法写入
     * @param setterMetas         setters写入
     */
    void writeTo(TypeSpec.Builder builder,
                 MethodSpec.Builder acceptMethodBuilder,
                 List<CreatorSetterMeta> setterMetas);
}
