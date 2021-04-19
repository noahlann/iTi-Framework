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

package org.lan.iti.cloud.codegen.repository.domain;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import lombok.AllArgsConstructor;
import org.lan.iti.cloud.codegen.NLDDDProcessor;
import org.lan.iti.codegen.support.TypeBuilderFactory;
import org.lan.iti.common.ddd.IDomainRepository;

import javax.annotation.Generated;
import javax.lang.model.element.Modifier;
import java.time.LocalDateTime;

/**
 * GenAggRepository 类型构建器工厂
 * BaseXXXRepository extends IAggregateRepository<ID, AggregateRoot>
 *
 * @author NorthLan
 * @date 2021-02-06
 * @url https://noahlan.com
 */
@AllArgsConstructor
public final class GenDomainRepositoryBuilderFactory implements TypeBuilderFactory {
    private final GenDomainRepositoryMeta domainRepositoryMeta;

    @Override
    public TypeSpec.Builder create() {
        return TypeSpec.interfaceBuilder(this.domainRepositoryMeta.getClsName())
                .addAnnotation(AnnotationSpec.builder(Generated.class)
                        .addMember("value", "\"" + NLDDDProcessor.class.getCanonicalName() + "\"")
                        .addMember("date", "\"" + LocalDateTime.now().toString() + "\"")
                        .addMember("comments", "\"This codes are generated automatically. Do not modify!\"")
                        .build())
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(IDomainRepository.class),
                        ClassName.get(domainRepositoryMeta.getAggType().asType())));
    }
}
