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

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import org.lan.iti.cloud.codegen.repository.domain.util.IndexUtils;
import org.lan.iti.codegen.TypeCollector;
import org.lan.iti.codegen.util.PackageBuilder;

import javax.lang.model.element.TypeElement;
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
public class GenDomainRepositoryMetaParser {
    private final TypeCollector typeCollector;

    public GenDomainRepositoryMeta parse(TypeElement typeElement, Annotation annotation) {
        if (annotation instanceof GenDomainRepository) {
            return parseFromGenAggRepository(typeElement, (GenDomainRepository) annotation);
        }
        throw new IllegalArgumentException();
    }

    private GenDomainRepositoryMeta parseFromGenAggRepository(TypeElement typeElement, GenDomainRepository annotation) {
        String pkgName = getPkgName(typeElement, annotation.pkgName());
        String clsName = getClsName(typeElement, annotation.clsName());
        List<IndexMeta> indexMetas = IndexUtils.getIndexMeta(typeElement, typeCollector);
        return new GenDomainRepositoryMeta(pkgName, clsName, typeElement, indexMetas);
    }

    private String getPkgName(TypeElement typeElement, String pkgName) {
        if (StrUtil.isNotBlank(pkgName)) {
            return pkgName;
        }
        return getDefaultPkgName(typeElement);
    }

    private String getDefaultPkgName(TypeElement typeElement) {
        // 取用相对位置
        String currentPkgName = typeElement.getEnclosingElement().toString();
        return PackageBuilder.from(currentPkgName)
                .parent()
                .child("facade.repository")
                .build();
    }

    private String getClsName(TypeElement typeElement, String clsName) {
        if (StrUtil.isNotEmpty(clsName)) {
            return clsName;
        }
        return getDefaultClsName(typeElement);
    }

    private String getDefaultClsName(TypeElement typeElement) {
        return "Base" + typeElement.getSimpleName().toString() + "Repository";
    }
}
