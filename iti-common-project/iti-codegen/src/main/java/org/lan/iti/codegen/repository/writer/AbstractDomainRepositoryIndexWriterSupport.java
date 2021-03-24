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

package org.lan.iti.codegen.repository.writer;

import lombok.AccessLevel;
import lombok.Getter;
import org.lan.iti.codegen.JavaSource;
import org.lan.iti.codegen.repository.GenDomainRepositoryMeta;
import org.lan.iti.codegen.repository.IndexMeta;
import org.lan.iti.codegen.repository.ParamElement;
import org.lan.iti.codegen.support.MethodWriter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AggRepository 方法写入支持
 * 当注解了 @Index 时,生成相关方法
 *
 * @author NorthLan
 * @date 2021-02-06
 * @url https://noahlan.com
 */
public abstract class AbstractDomainRepositoryIndexWriterSupport implements MethodWriter {
    @Getter(AccessLevel.PROTECTED)
    private final GenDomainRepositoryMeta repositoryMeta;
    private final Set<String> createdMethods = new HashSet<>();

    public AbstractDomainRepositoryIndexWriterSupport(GenDomainRepositoryMeta repositoryMeta) {
        this.repositoryMeta = repositoryMeta;
    }

    @Override
    public void writeTo(JavaSource javaSource) {
        for (IndexMeta indexMeta : repositoryMeta.getIndices()) {
            int paramSize = indexMeta.getParams().size();
            for (int i = 0; i < paramSize; ++i) {
                boolean isFull = i == paramSize - 1;
                List<ParamElement> paramElements = new ArrayList<>();
                for (int j = 0; j <= i; ++j) {
                    paramElements.add(indexMeta.getParams().get(j));
                }

                String methodKey = createMethodKey(paramElements);
                if (createdMethods.contains(methodKey)) {
                    continue;
                }
                createdMethods.add(methodKey);

                writeMethodTo(paramElements, javaSource, indexMeta.isUnique(), isFull);
            }
        }
    }

    private String createMethodKey(List<ParamElement> paramElements) {
        return formatName(paramElements);
    }

    protected abstract void writeMethodTo(List<ParamElement> paramElements, JavaSource javaSource, boolean unique, boolean isFull);

    protected String formatName(List<ParamElement> elements) {
        return elements.stream()
                .filter(ParamElement::isField)
                .map(ParamElement::getName)
                .map(name -> name.substring(0, 1).toUpperCase() + name.substring(1))
                .collect(Collectors.joining("And"));
    }
}
