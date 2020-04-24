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

import lombok.experimental.UtilityClass;
import org.lan.iti.common.core.util.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * VO对象绑定注解关系缓存管理类
 *
 * @author NorthLan
 * @date 2020-04-18
 * @url https://noahlan.com
 */
@UtilityClass
public class ParserCache {
    /**
     * VO-绑定注解缓存
     */
    private static Map<Class<?>, AnnotationGroup> allVoAnnotationCacheMap = new ConcurrentHashMap<>();

    /**
     * 获取指定class对应的Bind相关注解
     */
    public AnnotationGroup getAnnotationGroup(Class<?> voClass) {
        return allVoAnnotationCacheMap.computeIfAbsent(voClass, it -> {
            AnnotationGroup group = new AnnotationGroup();
            // 获取当前VO注解
            List<Field> fields = BeanUtils.extractAllFields(it);
            for (Field field : fields) {
                Annotation[] annotations = field.getDeclaredAnnotations();

                Class<?> setterObjClazz = field.getType();
                if (setterObjClazz.equals(List.class) || setterObjClazz.equals(Collection.class)) {
                    Type genericType = field.getGenericType();
                    if (genericType instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) genericType;
                        setterObjClazz = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                    }
                }

                for (Annotation annotation : annotations) {
                    group.add(field.getName(), setterObjClazz, annotation);
                }
            }
            return group;
        });
    }
}
