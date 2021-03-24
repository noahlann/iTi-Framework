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

package org.lan.iti.cloud.codegen.repository.jpa;

import com.squareup.javapoet.*;
import lombok.AllArgsConstructor;
import org.lan.iti.cloud.jpa.repository.BaseEntityGraphRepository;
import org.lan.iti.codegen.NLDDDProcessor;
import org.lan.iti.codegen.support.TypeBuilderFactory;
import org.lan.iti.codegen.util.TypeUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.data.repository.NoRepositoryBean;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;
import java.time.LocalDateTime;

/**
 * GenAggRepository 类型构建器工厂
 * <code>
 * BaseJpaXXXRepository<Po> extends BaseJpaXXXRepository, BaseEntityGraphRepository<ID, Po>
 * </code>
 *
 * @author NorthLan
 * @date 2021-02-06
 * @url https://noahlan.com
 */
@AllArgsConstructor
public final class GenJpaRepositoryBuilderFactory implements TypeBuilderFactory {
    private final GenJpaRepositoryMeta jpaRepositoryMeta;

    @Override
    public TypeSpec.Builder create() {
        // field
        FieldSpec.Builder fieldSpecBuilder = FieldSpec.builder(jpaRepositoryMeta.getTranslatorTypeName(),
                GenJpaRepositoryMeta.TRANSLATOR_FIELD_NAME, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
        if (jpaRepositoryMeta.isUseMapFactory()) {
            fieldSpecBuilder.initializer("$T.getMapper($T.class)", ClassName.get(Mappers.class), jpaRepositoryMeta.getTranslatorTypeName());
        } else {
            fieldSpecBuilder.initializer("$T.INSTANCE", jpaRepositoryMeta.getTranslatorTypeName());
        }

        return TypeSpec.interfaceBuilder(jpaRepositoryMeta.getClsName())
                .addAnnotation(AnnotationSpec.builder(Generated.class)
                        .addMember("value", "\"" + NLDDDProcessor.class.getCanonicalName() + "\"")
                        .addMember("date", "\"" + LocalDateTime.now().toString() + "\"")
                        .addMember("comments", "\"This codes are generated automatically. Do not modify!\"")
                        .build())
                .addAnnotation(NoRepositoryBean.class)
                .addModifiers(Modifier.PUBLIC)
                .addField(fieldSpecBuilder.build())
                .addSuperinterface(jpaRepositoryMeta.getRepoType().asType())
                .addSuperinterface(ParameterizedTypeName.get(
                        ClassName.get(BaseEntityGraphRepository.class),
                        jpaRepositoryMeta.getPoTypeName(),
                        ClassName.bestGuess(TypeUtils.getIdClassName(jpaRepositoryMeta.getRepoType()))));
    }
}
