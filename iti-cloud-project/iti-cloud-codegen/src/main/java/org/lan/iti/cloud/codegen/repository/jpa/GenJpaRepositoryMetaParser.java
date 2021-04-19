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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.squareup.javapoet.TypeName;
import lombok.AllArgsConstructor;
import org.lan.iti.codegen.TypeCollector;
import org.lan.iti.codegen.util.TypeUtils;
import org.lan.iti.common.ddd.IDomainRepository;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * AggRepositoryMeta 解析器
 *
 * @author NorthLan
 * @date 2021-02-06
 * @url https://noahlan.com
 */
@AllArgsConstructor
public class GenJpaRepositoryMetaParser {
    private final TypeCollector typeCollector;
    private final Types typeUtils;
    private final Elements elementUtils;

    public GenJpaRepositoryMeta parse(Element element, Annotation annotation) throws Exception {
        if (annotation instanceof GenJpaRepository) {
            return parseFromGenJpaRepository(element, (GenJpaRepository) annotation);
        }
        throw new IllegalArgumentException();
    }

    private GenJpaRepositoryMeta parseFromGenJpaRepository(Element element, GenJpaRepository annotation) throws Exception {
        // domainRepoClazz
        String domainRepoClsName = TypeUtils.getClassNameSafety(annotation::domainRepoClazz);
        // domainRepoType
        TypeElement domainRepoType = elementUtils.getTypeElement(domainRepoClsName);
        // pkgName
        String pkgName = getPkgName(element, annotation.pkgName());
        // clzName
        String clsName = getClsName(domainRepoType, annotation.clsName());
        // poClazz
        String poClsName = TypeUtils.getClassNameSafety(annotation::poClazz);
        TypeElement poClsType = elementUtils.getTypeElement(poClsName);
        // doClazz
        String doClsName = getDoClsName(domainRepoType.asType());
        TypeElement doClsType = elementUtils.getTypeElement(doClsName);
        // translatorClazz
        String translatorClsName = TypeUtils.getClassNameSafety(annotation::translatorClazz);
        TypeElement translatorClsType = elementUtils.getTypeElement(translatorClsName);
        boolean useMapFactory = annotation.useMapFactory();

        return new GenJpaRepositoryMeta(
                pkgName,
                clsName,
                domainRepoType,
                poClsType,
                doClsType,
                translatorClsType,
                useMapFactory,
                TypeName.get(poClsType.asType()),
                TypeName.get(doClsType.asType()),
                TypeName.get(translatorClsType.asType()));
    }

    private String getDoClsName(TypeMirror type) throws Exception {
        // domainRepoType 一定是 IDomainRepository 子类
        if (type instanceof DeclaredType) {
            DeclaredType declaredType = (DeclaredType) type;
            if (declaredType.asElement().asType()
                    .equals(elementUtils.getTypeElement(IDomainRepository.class.getCanonicalName()).asType())) {
                List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
                if (CollUtil.isNotEmpty(typeArguments)) {
                    return typeArguments.get(0).toString();
                }
                throw new Exception("error domainRepoClazz");
            }
            return getDoClsName(((TypeElement) declaredType.asElement()).getInterfaces().get(0));
        }
        return "";
    }

    private String getPkgName(Element element, String pkgName) {
        if (StrUtil.isNotEmpty(pkgName)) {
            return pkgName;
        }
        return getDefaultPkgName(element);
    }

    private String getDefaultPkgName(Element element) {
        if (element instanceof PackageElement) {
            return element.toString();
        }
        return element.getEnclosingElement().toString();
    }

    private String getClsName(TypeElement typeElement, String clsName) {
        if (StrUtil.isNotEmpty(clsName)) {
            return clsName;
        }
        return getDefaultClsName(typeElement);
    }

    private String getDefaultClsName(TypeElement typeElement) {
        return "BaseJpa" + typeElement.getSimpleName().toString().replace("Repository", "") + "Repository";
    }
}
