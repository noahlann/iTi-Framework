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

package org.lan.iti.cloud.codegen.converter;

import cn.hutool.core.util.StrUtil;
import com.squareup.javapoet.*;
import org.lan.iti.codegen.AbstractProcessorPlugin;
import org.lan.iti.codegen.JavaSource;
import org.lan.iti.codegen.NLDDDProcessor;

import javax.annotation.Generated;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.lang.annotation.Annotation;
import java.time.LocalDateTime;

/**
 * Jpa 枚举转换器插件
 *
 * @author NorthLan
 * @date 2021-02-01
 * @url https://noahlan.com
 */
@SuppressWarnings("unchecked")
public final class GenJpaEnumConverterPlugin extends AbstractProcessorPlugin {

    @Override
    protected void process(TypeElement typeElement, Annotation annotation) {
        GenJpaEnumConverter converterAnnotation = (GenJpaEnumConverter) annotation;
        String pkg = createPkgName(typeElement, converterAnnotation.pkgName());

        String className = createClassName(typeElement);

        String fieldName = converterAnnotation.value();
        Element fieldElement;
        if (StrUtil.isBlank(fieldName)) {
            // 取第一个变量
            fieldElement = getFirstFieldElement(typeElement);
        } else {
            fieldElement = getFieldElementByName(typeElement, fieldName);
        }
        TypeName fieldTypeName = getTypeNameByElement(fieldElement);

        TypeSpec.Builder typeSpecBuilder = createTypeBuilder(typeElement, fieldTypeName, className);

        typeSpecBuilder.addMethod(createConvertToDatabaseMethod(typeElement, fieldElement, fieldTypeName));
        typeSpecBuilder.addMethod(createConvertToEntityMethod(typeElement, fieldElement, fieldTypeName));

        getJavaSourceCollector().register(new JavaSource(pkg, className, typeSpecBuilder));
    }

    private Element getFirstFieldElement(TypeElement typeElement) {
        return typeElement.getEnclosedElements().stream()
                .filter(it -> it.getKind() == ElementKind.FIELD).findFirst().orElse(null);
    }

    private Element getFieldElementByName(TypeElement typeElement, String fieldName) {
        return typeElement.getEnclosedElements().stream()
                .filter(it ->
                        it.getKind() == ElementKind.FIELD
                                && StrUtil.equals(it.getSimpleName().toString(), fieldName)).findFirst().orElse(null);
    }

    private TypeName getTypeNameByElement(Element element) {
        return element == null ? TypeName.INT : TypeName.get(element.asType());
    }

    private String getGetterName(Element element) {
        return getGetterName(element.getSimpleName().toString());
    }

    private String getGetterName(String element) {
        return "get" + StrUtil.upperFirst(element);
    }

    private MethodSpec createConvertToEntityMethod(TypeElement typeElement,
                                                   Element firstFieldElement,
                                                   TypeName fieldTypeName) {
        return MethodSpec.methodBuilder("convertToEntityAttribute")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(fieldTypeName.box(), "i")
                .returns(TypeName.get(typeElement.asType()))
                .addStatement("if (i == null) return null")
                .addCode(CodeBlock.builder()
                        .add("for ($T value : $T.values()){\n", TypeName.get(typeElement.asType()), TypeName.get(typeElement.asType()))
                        .add("\tif (value.$L() == i){\n", getGetterName(firstFieldElement))
                        .add("\t\treturn value; \n")
                        .add("\t}\n")
                        .add("}\n")
                        .add("return null;\n")
                        .build())
                .build();
    }

    private MethodSpec createConvertToDatabaseMethod(TypeElement typeElement, Element firstFieldElement, TypeName fieldTypeName) {
        return MethodSpec.methodBuilder("convertToDatabaseColumn")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(TypeName.get(typeElement.asType()), "i")
                .returns(fieldTypeName.box())
                .addStatement("return i == null ? null : i.$L()", getGetterName(firstFieldElement))
                .build();
    }

    private TypeSpec.Builder createTypeBuilder(TypeElement typeElement, TypeName fieldTypeName, String className) {
        ParameterizedTypeName attributeConverter = ParameterizedTypeName.get(
                ClassName.get(AttributeConverter.class),
                TypeName.get(typeElement.asType()), fieldTypeName.box());

        return TypeSpec.classBuilder(className)
                .addSuperinterface(attributeConverter)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addAnnotation(AnnotationSpec.builder(Generated.class)
                        .addMember("value", "\"" + NLDDDProcessor.class.getCanonicalName() + "\"")
                        .addMember("date", "\"" + LocalDateTime.now().toString() + "\"")
                        .addMember("comments", "\"This codes are generated automatically. Do not modify!\"")
                        .build())
                .addAnnotation(AnnotationSpec.builder(Converter.class)
                        .addMember("autoApply", "true")
                        .build());
    }

    private String createClassName(TypeElement typeElement) {
        return "Jpa" + typeElement.getSimpleName() + "Converter";
    }

    private String createPkgName(TypeElement typeElement, String configPkgName) {
        return StrUtil.isNotEmpty(configPkgName) ? configPkgName : typeElement.getEnclosingElement().toString();
    }

    @Override
    public Class<Annotation>[] applyAnnCls() {
        return new Class[]{GenJpaEnumConverter.class};
    }

    @Override
    public Class<Annotation>[] ignoreAnnCls() {
        return new Class[0];
    }
}
