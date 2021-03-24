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

package org.lan.iti.common.core;

/**
 * 分层模型的类型转换，DTO -> Creator -> Entity <-> PO.
 * <p>
 * <p>使用{@code MapStruct} 或 {@code Selma}进行声明式类型转换</p>
 *
 * @param <Source> 源类型, DDD中建议为Do
 * @param <Target> 目标类型, DDD中建议为Po
 * @author NorthLan
 * @date 2021-03-01
 * @url https://noahlan.com
 */
public interface IBaseTranslator<Source, Target> {

    /**
     * 映射同名属性，可以通过覆盖来实现更复杂的映射逻辑.
     * <p>
     * <p>包含常用的Java类型自动转换</p>
     *
     * @param source 源类型
     * @return 目标类型
     */
    Target translate(Source source);
}
