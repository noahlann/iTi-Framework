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

import cn.hutool.core.collection.CollUtil;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * @author NorthLan
 * @date 2021-02-01
 * @url https://noahlan.com
 */
public abstract class AbstractGeneratorProcessor extends AbstractProcessor {
    private final TypeCollector typeCollector = new TypeCollector();

    /**
     * 获取所有的插件
     */
    protected abstract ProcessorPlugin[] getPlugins();

    protected Types typeUtils;
    protected Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        for (ProcessorPlugin plugin : getPlugins()) {
            plugin.init(typeCollector, typeUtils, elementUtils);
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        // 自定义支持的注解类型
        Set<String> types = new HashSet<>();
        for (ProcessorPlugin plugin : this.getPlugins()) {
            for (Class<Annotation> cls : plugin.applyAnnCls()) {
                types.add(cls.getName());
            }
        }
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (CollUtil.isEmpty(annotations)) {
            return true;
        }
        typeCollector.syncForm(roundEnv);
        for (ProcessorPlugin plugin : this.getPlugins()) {
            try {
                System.out.printf("process by plugin %s%n", plugin.getClass());
                plugin.process(annotations, roundEnv);
                plugin.write(this.processingEnv.getFiler());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
