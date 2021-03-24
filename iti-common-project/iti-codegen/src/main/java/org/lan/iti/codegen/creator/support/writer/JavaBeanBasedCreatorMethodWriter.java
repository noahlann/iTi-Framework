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

package org.lan.iti.codegen.creator.support.writer;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.lan.iti.codegen.annotation.Description;
import org.lan.iti.codegen.creator.support.meta.CreatorSetterMeta;
import org.lan.iti.codegen.util.TypeUtils;

import javax.lang.model.element.Modifier;
import java.util.List;

/**
 * java-bean形式的方法代码生成
 *
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
public class JavaBeanBasedCreatorMethodWriter implements CreatorWriter {
    @Override
    public void writeTo(TypeSpec.Builder builder, MethodSpec.Builder acceptMethodBuilder, List<CreatorSetterMeta> setterMetas) {
        for (CreatorSetterMeta setterMeta : setterMetas) {
            String fieldName = TypeUtils.getFieldName(setterMeta.name());
            String targetSetterName = "set" + fieldName;
            String getterName = "get" + fieldName;
            acceptMethodBuilder.addStatement("target.$L($L())", targetSetterName, getterName);

            // field
            FieldSpec fieldSpec = FieldSpec.builder(setterMeta.type(), setterMeta.name(), Modifier.PRIVATE)
                    .addAnnotation(AnnotationSpec.builder(Description.class)
                            .addMember("value", "$S", setterMeta.description())
                            .build())
                    .build();
            builder.addField(fieldSpec);
        }
    }
}
