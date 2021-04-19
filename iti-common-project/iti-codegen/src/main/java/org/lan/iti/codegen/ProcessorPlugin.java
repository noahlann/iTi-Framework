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

package org.lan.iti.codegen;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * 处理插件
 *
 * @author NorthLan
 * @date 2021-02-01
 * @url https://noahlan.com
 */
public interface ProcessorPlugin {
    /**
     * 应用注解类
     *
     * @param <A> 注解类型泛型
     * @return 类描述数组
     */
    <A extends Annotation> Class<A>[] applyAnnCls();

    /**
     * 忽略注解类
     *
     * @param <A> 注解类型泛型
     * @return 类描述数组
     */
    <A extends Annotation> Class<A>[] ignoreAnnCls();

    /**
     * 初始化
     *
     * @param typeCollector 类型收集器
     */
    void init(TypeCollector typeCollector, Types typeUtils, Elements elementUtils);

    /**
     * 处理
     *
     * @param annotations 注解
     * @param roundEnv    环境
     */
    void process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws Exception;

    /**
     * 写入
     *
     * @param filer 过滤器
     */
    void write(Filer filer);
}
