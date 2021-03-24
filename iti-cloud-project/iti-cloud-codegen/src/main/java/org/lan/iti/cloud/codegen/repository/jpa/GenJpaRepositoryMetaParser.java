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

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.squareup.javapoet.ClassName;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.ddd.IDomainRepository;
import org.lan.iti.codegen.TypeCollector;
import org.lan.iti.codegen.util.TypeUtils;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.lang.annotation.Annotation;

/**
 * AggRepositoryMeta 解析器
 *
 * @author NorthLan
 * @date 2021-02-06
 * @url https://noahlan.com
 */
@Slf4j
@AllArgsConstructor
public class GenJpaRepositoryMetaParser {
    private final TypeCollector typeCollector;
    private final Types typeUtils;
    private final Elements elementUtils;

    public GenJpaRepositoryMeta parse(TypeElement typeElement, Annotation annotation) {
        if (annotation instanceof GenJpaRepository) {
            return parseFromGenJpaRepository(typeElement, (GenJpaRepository) annotation);
        }
        throw new IllegalArgumentException();
    }

    private GenJpaRepositoryMeta parseFromGenJpaRepository(TypeElement typeElement, GenJpaRepository annotation) {
        TypeElement repoType = (TypeElement) typeUtils.asElement(typeElement.getInterfaces().get(0));
        String pkgName = getPkgName(typeElement, annotation.pkgName());
        String clsName = getClsName(repoType, annotation.clsName());
        // poClazz
        Class<?> poClazz = getPoClazz(annotation);
        // doClazz
        Class<?> doClazz = getDoClazz(typeElement);
        // translatorClazz
        Class<?> translatorClazz = getTranslatorClazz(annotation);
        boolean useMapFactory = annotation.useMapFactory();
        return new GenJpaRepositoryMeta(
                pkgName,
                clsName,
                repoType,
                typeCollector.getByName(poClazz.getCanonicalName()),
                typeCollector.getByName(doClazz.getCanonicalName()),
                typeCollector.getByName(translatorClazz.getCanonicalName()),
                useMapFactory,
                ClassName.get(poClazz),
                ClassName.get(doClazz),
                ClassName.get(translatorClazz));
    }

    private Class<?> getTranslatorClazz(GenJpaRepository annotation) {
        Class<?> clazz = null;
        String clsName;
        try {
            clsName = annotation.translatorClazz().getCanonicalName();
        } catch (MirroredTypeException e) {
            clsName = e.getTypeMirror().toString();
        }
        if (StrUtil.isNotEmpty(clsName)) {
            try {
                clazz = Class.forName(clsName);
            } catch (ClassNotFoundException ignore) {
            }
        }
        return clazz;
    }

    private Class<?> getPoClazz(GenJpaRepository annotation) {
        Class<?> clazz = null;
        String clsName;
        try {
            clsName = annotation.poClazz().getCanonicalName();
        } catch (MirroredTypeException e) {
            clsName = e.getTypeMirror().toString();
        }
        if (StrUtil.isNotEmpty(clsName)) {
            try {
                clazz = Class.forName(clsName);
            } catch (ClassNotFoundException ignore) {
            }
        }
        return clazz;
    }

    private Class<?> getDoClazz(TypeElement element) {
        return getDoClazz(element.getInterfaces().get(0));
    }

    private Class<?> getDoClazz(TypeMirror mirror) {
        Class<?> tmp = TypeUtils.getClass(mirror);
        if (tmp != null && IDomainRepository.class.isAssignableFrom(tmp)) {
            return ClassUtil.getTypeArgument(tmp);
        }
        return getDoClazz(((TypeElement) typeUtils.asElement(mirror)).getInterfaces().get(0));
    }

    private String getPkgName(TypeElement typeElement, String pkgName) {
        if (StrUtil.isNotEmpty(pkgName)) {
            return pkgName;
        }
        return getDefaultPkgName(typeElement);
    }

    private String getDefaultPkgName(TypeElement typeElement) {
        return typeElement.getEnclosingElement().toString();
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
