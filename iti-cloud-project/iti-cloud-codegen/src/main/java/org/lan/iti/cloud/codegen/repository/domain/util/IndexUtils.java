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

package org.lan.iti.cloud.codegen.repository.domain.util;

import com.google.common.collect.Sets;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.codegen.TypeCollector;
import org.lan.iti.cloud.codegen.repository.domain.Index;
import org.lan.iti.cloud.codegen.repository.domain.IndexMeta;
import org.lan.iti.cloud.codegen.repository.domain.Indexes;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * @author NorthLan
 * @date 2021-02-06
 * @url https://noahlan.com
 */
@UtilityClass
public class IndexUtils {

    public List<IndexMeta> getIndexMeta(TypeElement aggType, TypeCollector typeCollector) {
        List<Index> indices = findIndexes(aggType);
        Map<String, VariableElement> fieldMap = findAllFields(aggType, typeCollector).stream()
                .collect(toMap(variableElement -> variableElement.getSimpleName().toString(), variableElement -> variableElement));

        List<IndexMeta> indexMetas = new ArrayList<>();
        for (Index index : indices) {
            IndexMeta indexMeta = new IndexMeta(index.unique());
            for (String fieldName : index.value()) {
                VariableElement variableElement = fieldMap.get(fieldName);
                if (variableElement != null) {
                    indexMeta.addParam(variableElement);
                } else {
                    System.err.println("failed to find field " + fieldName);
                }
            }
            indexMetas.add(indexMeta);
        }
        return indexMetas;
    }

    private List<Index> findIndexes(Element element) {
        List<Index> indices = new ArrayList<>();
        Indexes indexes = element.getAnnotation(Indexes.class);
        if (indexes != null) {
            indices.addAll(Arrays.asList(indexes.value()));
        } else {
            Index index = element.getAnnotation(Index.class);
            if (index != null) {
                indices.add(index);
            }
        }
        return indices;
    }

    private Set<VariableElement> findAllFields(Element element, TypeCollector typeCollector) {
        Set<VariableElement> result = Sets.newHashSet();
        Element tmp = element;
        do {
            result.addAll(findFields(tmp));
            tmp = getSuperType(tmp, typeCollector);
        } while (tmp != null);
        return result;
    }

    private Element getSuperType(Element element, TypeCollector typeCollector) {
        if (element instanceof TypeElement) {
            TypeMirror typeMirror = ((TypeElement) element).getSuperclass();
            if (typeMirror == null) {
                return null;
            }
            return typeCollector.getByName(typeMirror.toString());
        }
        return null;
    }

    private Set<VariableElement> findFields(Element element) {
        return new HashSet<>(ElementFilter.fieldsIn(element.getEnclosedElements()));
    }
}
