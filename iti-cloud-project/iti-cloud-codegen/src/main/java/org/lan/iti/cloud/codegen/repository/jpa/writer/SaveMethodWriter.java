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

import com.squareup.javapoet.MethodSpec;
import lombok.AllArgsConstructor;
import org.lan.iti.codegen.JavaSource;
import org.lan.iti.cloud.codegen.repository.jpa.GenJpaRepositoryMeta;
import org.lan.iti.codegen.support.MethodWriter;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.model.element.Modifier;

/**
 * @author NorthLan
 * @date 2021-03-01
 * @url https://noahlan.com
 */
@AllArgsConstructor
public class SaveMethodWriter implements MethodWriter {
    private final GenJpaRepositoryMeta repositoryMeta;

    @Override
    public void writeTo(JavaSource javaSource) {
        String methodName = "save";
        String paramName = "entity";

        MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
                .addAnnotation(Override.class)
                .addAnnotation(Transactional.class)
                .returns(repositoryMeta.getDoTypeName())
                .addParameter(repositoryMeta.getDoTypeName(), paramName);

        methodSpecBuilder.addStatement("$T po = $L.toPo($L)",
                repositoryMeta.getPoTypeName(),
                GenJpaRepositoryMeta.TRANSLATOR_FIELD_NAME,
                paramName);

        methodSpecBuilder.addStatement("po = this.save(po)");
        // 目前 持久化方法中 po 仅 id字段 需要反馈到 do
        methodSpecBuilder.addStatement("entity.setId(po.getId())");
        methodSpecBuilder.addStatement("return entity");

        javaSource.addMethod(methodSpecBuilder.build());
    }
}
