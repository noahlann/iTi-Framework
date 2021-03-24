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

package org.lan.iti.codegen.creator;

import cn.hutool.core.util.StrUtil;
import com.squareup.javapoet.*;
import lombok.Data;
import org.lan.iti.codegen.AbstractProcessorPlugin;
import org.lan.iti.codegen.JavaSource;
import org.lan.iti.codegen.NLDDDProcessor;
import org.lan.iti.codegen.creator.support.meta.CreatorMeta;
import org.lan.iti.codegen.creator.support.parser.CreatorMetaParser;
import org.lan.iti.codegen.creator.support.writer.JavaBeanBasedCreatorMethodWriter;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;
import java.time.LocalDateTime;

/**
 * GenCreator 插件
 *
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
public class GenCreatorPlugin extends AbstractProcessorPlugin {
    private final CreatorMetaParser metaParser = new CreatorMetaParser();

    @Override
    protected void process(TypeElement typeElement, Annotation t) {
        GenCreatorParser parser = new GenCreatorParser(typeElement);
        parser.read(t);

        // fields
        String packageName = parser.getPackageName();
        String className = parser.getClassName();
        String parentClassName = parser.getParentClassName();
        boolean genClass = parser.isGenClass();

        // Type
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(className)
                .addAnnotation(AnnotationSpec.builder(Generated.class)
                        .addMember("value", "\"" + NLDDDProcessor.class.getCanonicalName() + "\"")
                        .addMember("date", "\"" + LocalDateTime.now().toString() + "\"")
                        .addMember("comments", "\"This codes are generated automatically. Do not modify!\"")
                        .build())
                .addAnnotation(Data.class)
                .addModifiers(Modifier.PUBLIC);
        if (!genClass) {
            typeSpecBuilder.addModifiers(Modifier.ABSTRACT)
                    .addTypeVariable(TypeVariableName.get("T extends " + className));
        }
        // superclass
        if (StrUtil.isNotEmpty(parentClassName)) {
            ClassName parent = ClassName.bestGuess(parentClassName);
            TypeName typeName = TypeVariableName.get("T");
            ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(parent, typeName);
            typeSpecBuilder.superclass(parameterizedTypeName);
        }
        // method
        MethodSpec.Builder acceptMethodBuilder = MethodSpec.methodBuilder("accept")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.get(typeElement.asType()))
                .addParameter(ParameterSpec.builder(TypeName.get(typeElement.asType()), "target")
                        .build());
        if (StrUtil.isNotEmpty(parentClassName)) {
            acceptMethodBuilder.addStatement("super.accept(target)");
        }
        CreatorMeta creatorMeta = this.metaParser.parse(typeElement);
        new JavaBeanBasedCreatorMethodWriter()
                .writeTo(typeSpecBuilder, acceptMethodBuilder, creatorMeta.getMethodMetas());
        // 为Method添加return
        acceptMethodBuilder.addStatement("return target");
        // 将accept方法添加到type中
        typeSpecBuilder.addMethod(acceptMethodBuilder.build());
        // 注册源码
        getJavaSourceCollector().register(new JavaSource(packageName, className, typeSpecBuilder));
    }

    @Override
    public <A extends Annotation> Class<A>[] applyAnnCls() {
        return new Class[]{GenCreator.class};
    }

    @Override
    public <A extends Annotation> Class<A>[] ignoreAnnCls() {
        return new Class[]{GenCreatorIgnore.class};
    }
}
