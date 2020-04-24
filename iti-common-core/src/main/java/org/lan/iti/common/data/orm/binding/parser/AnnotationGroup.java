/*
 *
 *  * Copyright (c) [2019-2020] [NorthLan](lan6995@gmail.com)
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

package org.lan.iti.common.data.orm.binding.parser;

import cn.hutool.core.collection.CollUtil;
import lombok.Getter;
import org.lan.iti.common.data.orm.binding.annotation.BindEntity;
import org.lan.iti.common.data.orm.binding.annotation.BindEntityList;
import org.lan.iti.common.data.orm.binding.annotation.BindField;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * VO绑定注解的归类分组，用于缓存解析后的结果
 *
 * @author NorthLan
 * @date 2020-04-18
 * @url https://noahlan.com
 */
@Getter
public class AnnotationGroup {
    /**
     * 字段关联注解
     */
    private List<FieldAnnotation> fieldAnnotations;

    /**
     * 实体关联注解
     */
    private List<FieldAnnotation> entityAnnotations;

    /**
     * 实体集合关联注解
     */
    private List<FieldAnnotation> entityListAnnotations;

    /**
     * 添加注解关系
     *
     * @param fieldName  字段名
     * @param fieldClass 字段类
     * @param annotation 注解
     */
    public void add(String fieldName, Class<?> fieldClass, Annotation annotation) {
        if (annotation instanceof BindField) {
            if (fieldAnnotations == null) {
                fieldAnnotations = new ArrayList<>();
            }
            fieldAnnotations.add(new FieldAnnotation(fieldName, fieldClass, annotation));
        } else if (annotation instanceof BindEntity) {
            if (entityAnnotations == null) {
                entityAnnotations = new ArrayList<>();
            }
            entityAnnotations.add(new FieldAnnotation(fieldName, fieldClass, annotation));
        } else if (annotation instanceof BindEntityList) {
            if (entityListAnnotations == null) {
                entityListAnnotations = new ArrayList<>();
            }
            entityListAnnotations.add(new FieldAnnotation(fieldName, fieldClass, annotation));
        }
    }

    /**
     * 是否不为空
     */
    public boolean isNotEmpty() {
        return CollUtil.isNotEmpty(fieldAnnotations) ||
                CollUtil.isNotEmpty(entityAnnotations) ||
                CollUtil.isNotEmpty(entityListAnnotations);
    }
}
