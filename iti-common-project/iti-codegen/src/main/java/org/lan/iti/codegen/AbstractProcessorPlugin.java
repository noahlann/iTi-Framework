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
import com.squareup.javapoet.JavaFile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author NorthLan
 * @date 2021-02-01
 * @url https://noahlan.com
 */
@Getter
@Setter(AccessLevel.PRIVATE)
public abstract class AbstractProcessorPlugin implements ProcessorPlugin {
    private final JavaSourceCollector javaSourceCollector = new JavaSourceCollector();
    private TypeCollector typeCollector;
    private Types typeUtils;
    private Elements elementUtils;

    protected abstract void process(TypeElement typeElement, Annotation t) throws Exception;

    protected void process(PackageElement packageElement, Annotation t) throws Exception {
    }

    @Override
    public void init(TypeCollector typeCollector, Types typeUtils, Elements elementUtils) {
        this.typeCollector = typeCollector;
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
    }

    @Override
    public void process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) throws Exception {
        for (Element element : roundEnv.getRootElements()) {
            if (!(element instanceof TypeElement)) {
                if (element instanceof PackageElement) {
                    if (CollUtil.isEmpty(element.getAnnotationMirrors())) {
                        continue;
                    }
                } else {
                    continue;
                }
            }

            // 1. package 且其上标记有注解
            // 2. 其它普通类型: 类、接口、enum等

            for (Class<Annotation> ann : ignoreAnnCls()) {
                if (element.getAnnotation(ann) != null) {
                    break;
                }
            }

            for (Class<Annotation> ann : applyAnnCls()) {
                Annotation annotation = element.getAnnotation(ann);
                Element newElement = element;
                if (annotation == null) {
                    for (Element enclosedElement : element.getEnclosedElements()) {
                        if (enclosedElement.getKind() == ElementKind.CLASS ||
                                enclosedElement.getKind() == ElementKind.INTERFACE) {
                            annotation = enclosedElement.getAnnotation(ann);
                            if (annotation != null) {
                                newElement = enclosedElement;
                                process((TypeElement) newElement, annotation);
                            }
                        }
                    }
                } else {
                    if (newElement.getKind() == ElementKind.PACKAGE) {
                        process((PackageElement) newElement, annotation);
                    } else {
                        process((TypeElement) newElement, annotation);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public final void write(Filer filer) {
        javaSourceCollector.getAllJavaSource()
                .forEach(javaSource -> createJavaFile(filer, javaSource));
    }

    private void createJavaFile(Filer filer, JavaSource javaSource) {
        try {
            JavaFile javaFile = JavaFile.builder(javaSource.getPkgName(), javaSource.getTypeSpec())
                    .addFileComment(" This codes are generated automatically. Do not modify!")
                    .build();
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
