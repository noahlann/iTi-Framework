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

package org.lan.iti.cloud.codegen.repository.jpa.writer;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import lombok.AllArgsConstructor;
import org.lan.iti.codegen.JavaSource;
import org.lan.iti.cloud.codegen.repository.jpa.GenJpaRepositoryMeta;
import org.lan.iti.codegen.support.MethodWriter;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author NorthLan
 * @date 2021-03-01
 * @url https://noahlan.com
 */
@AllArgsConstructor
public class GetAllByIdMethodWriter implements MethodWriter {
    private final GenJpaRepositoryMeta repositoryMeta;

    @Override
    public void writeTo(JavaSource javaSource) {
        String methodName = "getAllById";
        String paramName = "ids";

        MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
                .addAnnotation(Override.class)
                .addAnnotation(Transactional.class)
                .returns(ParameterizedTypeName.get(
                        ClassName.get(List.class),
                        repositoryMeta.getDoTypeName()))
                .addParameter(ParameterizedTypeName.get(ClassName.get(Iterable.class), ClassName.get(String.class)), paramName);

        methodSpecBuilder.addStatement("$T result = new $T()",
                ParameterizedTypeName.get(ClassName.get(List.class),
                        repositoryMeta.getDoTypeName()),
                ParameterizedTypeName.get(ClassName.get(ArrayList.class),
                        repositoryMeta.getDoTypeName()));
        methodSpecBuilder.addCode(CodeBlock.builder()
                .add("for ($T po : this.findAllById($L)) {\n", repositoryMeta.getPoTypeName(), paramName)
                .addStatement("$T.ofNullable($T.createWith($L.toCreator(po))).ifPresent(result::add)",
                        ClassName.get(Optional.class),
                        repositoryMeta.getDoTypeName(),
                        GenJpaRepositoryMeta.TRANSLATOR_FIELD_NAME)
                .add("}\n")
                .build());
        methodSpecBuilder.addStatement("return result");

        javaSource.addMethod(methodSpecBuilder.build());
    }
}
