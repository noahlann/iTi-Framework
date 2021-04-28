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

import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * 分层模型的类型转换，Command -> Do <-> PO.
 * <p>
 * <p>使用{@code MapStruct} 或 {@code Selma}进行声明式类型转换</p>
 *
 * @param <Source> 源类型
 * @param <Target> 目标类型
 * @author NorthLan
 * @date 2021-03-01
 * @url https://noahlan.com
 */
public interface IBaseTranslator<Source, Target> {
    interface MethodName {
        String TO_TARGET = "toTarget";
        String TO_SOURCE = "toSource";
        String TO_TARGET_LIST = "toTargetList";
        String TO_SOURCE_LIST = "toSourceList";
    }

    /**
     * 映射同名属性，可以通过覆盖来实现更复杂的映射逻辑.
     * <p>
     * <p>包含常用的Java类型自动转换</p>
     *
     * @param source 源类型
     * @return 目标类型
     */
    @Named(MethodName.TO_TARGET)
    Target toTarget(Source source);

    /**
     * 映射同名属性，可以通过覆盖来实现更复杂的映射逻辑.
     * <p>
     * <p>包含常用的Java类型自动转换</p>
     *
     * @param target 目标类型
     * @return 源类型
     */
    @Named(MethodName.TO_SOURCE)
    Source toSource(Target target);

    /**
     * 映射同名属性，可以通过覆盖来实现更复杂的映射逻辑.
     * <p>
     * <p>包含常用的Java类型自动转换</p>
     *
     * @param sources 源类型列表
     * @return 目标类型列表
     */
    @Named(MethodName.TO_TARGET_LIST)
    List<Target> toTargetList(List<Source> sources);

    /**
     * 映射同名属性，可以通过覆盖来实现更复杂的映射逻辑.
     * <p>
     * <p>包含常用的Java类型自动转换</p>
     *
     * @param targets 目标类型列表
     * @return 源类型列表
     */
    @Named(MethodName.TO_SOURCE_LIST)
    List<Source> toSourceList(List<Target> targets);

    /**
     * 复制非null属性到 target
     *
     * @param source 左值
     * @param target 右值
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void copyPropertiesNonNullTarget(Source source, @MappingTarget Target target);

    /**
     * 复制非null属性到 source
     *
     * @param source 右值
     * @param target 左值
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void copyPropertiesNonNullSource(Target target, @MappingTarget Source source);
}
