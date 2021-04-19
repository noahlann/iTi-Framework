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
import com.squareup.javapoet.TypeName;
import lombok.AllArgsConstructor;
import org.lan.iti.cloud.codegen.repository.jpa.GenJpaRepositoryMeta;
import org.lan.iti.cloud.util.BeanUtils;
import org.lan.iti.codegen.JavaSource;
import org.lan.iti.codegen.support.MethodWriter;
import org.lan.iti.common.core.exception.ServiceException;
import org.springframework.transaction.annotation.Transactional;

import javax.lang.model.element.Modifier;

/**
 * @author NorthLan
 * @date 2021-03-01
 * @url https://noahlan.com
 */
@AllArgsConstructor
public class UpdateMethodWriter implements MethodWriter {
    private final GenJpaRepositoryMeta repositoryMeta;

    @Override
    public void writeTo(JavaSource javaSource) {
        String methodName = "update";
        String paramName = "entity";

        MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(methodName)
                .addModifiers(Modifier.PUBLIC, Modifier.DEFAULT)
                .addAnnotation(Override.class)
                .addAnnotation(Transactional.class)
                .returns(TypeName.VOID)
                .addParameter(repositoryMeta.getDoTypeName(), paramName);

        methodSpecBuilder.addStatement("$T po = this.findById($L.getId()).orElseThrow(() -> new $T(-1, $L))",
                repositoryMeta.getPoTypeName(),
                paramName,
                TypeName.get(ServiceException.class),
                "\"不存在实体，更新失败\"");
        // 复制非null属性至Po
        methodSpecBuilder.addStatement("$T.copyPropertiesNonNull($L, po)",
                TypeName.get(BeanUtils.class),
                paramName);
        methodSpecBuilder.addStatement("this.save(po)");

        javaSource.addMethod(methodSpecBuilder.build());
    }
}
