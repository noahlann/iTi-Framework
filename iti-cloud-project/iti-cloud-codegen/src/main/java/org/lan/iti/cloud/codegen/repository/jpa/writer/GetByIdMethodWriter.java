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
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import lombok.AllArgsConstructor;
import org.lan.iti.codegen.JavaSource;
import org.lan.iti.cloud.codegen.repository.jpa.GenJpaRepositoryMeta;
import org.lan.iti.codegen.support.MethodWriter;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.model.element.Modifier;
import java.util.Optional;

/**
 * GetById方法写入
 * <p>
 * <p>Optional<A> getById(String id);</p>
 * <p>return Optional.ofNullable(User.createWith(translator.toCreator(findById(id).orElse(null))));</p>
 *
 * @author NorthLan
 * @date 2021-03-01
 * @url https://noahlan.com
 */
@AllArgsConstructor
public class GetByIdMethodWriter implements MethodWriter {
    private final GenJpaRepositoryMeta repositoryMeta;

    @Override
    public void writeTo(JavaSource javaSource) {
        String methodName = "getById";
        String paramName = "id";

        MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
                .addAnnotation(Override.class)
                .addAnnotation(Transactional.class)
                .returns(ParameterizedTypeName.get(
                        ClassName.get(Optional.class),
                        repositoryMeta.getDoTypeName()))
                .addParameter(TypeName.get(String.class), paramName);

        methodSpecBuilder.addStatement("return $T.ofNullable($T.createWith($L.toCreator(this.findById($L).orElse(null))))",
                ClassName.get(Optional.class),
                repositoryMeta.getDoTypeName(),
                GenJpaRepositoryMeta.TRANSLATOR_FIELD_NAME,
                paramName);

        javaSource.addMethod(methodSpecBuilder.build());
    }
}
