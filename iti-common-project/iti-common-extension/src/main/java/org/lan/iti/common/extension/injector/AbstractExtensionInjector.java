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

package org.lan.iti.common.extension.injector;

import cn.hutool.core.exceptions.UtilException;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.util.ReflectUtil;
import org.lan.iti.common.core.util.StringUtil;
import org.lan.iti.common.extension.ExtensionInjector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @author NorthLan
 * @date 2021/8/16
 * @url https://blog.noahlan.com
 */
@Slf4j
public abstract class AbstractExtensionInjector implements ExtensionInjector {
    private static final String LOG_PREFIX = ExtensionInjector.class.getSimpleName();

    @Override
    public <T> T inject(T instance, Field[] fields, Method[] setters) {
        // 1. 通过field直接注入，仅限于注解了Resource的属性
        for (Field field : fields) {
            // 已注入过
            if (ReflectUtil.getFieldValue(instance, field) != null) {
                continue;
            }
            if (this.isInjectByField(field)) {
                String fieldTypeName = this.getTypeName(field);
                this.injectByField(instance, field, fieldTypeName);
            } else {
                // 检查是否具有同名setter，即set[FieldName]
                for (Method setter : setters) {
                    String setFieldName = ReflectUtil.getSetterName(setter);
                    if (StringUtil.equals(setFieldName, field.getName())) {
                        // 注入 by setter
                        this.injectBySetter(instance, setter);
                    }
                }
            }
        }
        return instance;
    }

    protected abstract boolean isInjectByField(Field field);

    protected abstract String getTypeName(Field field);

    /**
     * 通过type加载列表
     *
     * @param type 待加载类型
     * @return 实例列表
     */
    protected abstract Collection loadListByType(Class type);

    /**
     * 通过type和name加载列表
     *
     * @param type 待加载类型
     * @param name 待加载类名(此类名从getTypeName来)
     * @return 实例
     */
    protected abstract Object loadByName(Class type, String name);

    /**
     * 通过type加载一个实例对象
     *
     * @param type 待加载类型
     * @return 实例
     */
    protected abstract Object loadByType(Class type);

    protected Object createParameterObj(Type fieldType, String name) {
        Object paramObj = null;
        Class fieldClass = null;
        // list set collection
        boolean isCollection = false;
        // TODO map

        if (fieldType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) fieldType;
            Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            if (Collection.class.isAssignableFrom(rawType)) {
                isCollection = true;
                fieldClass = (Class) parameterizedType.getActualTypeArguments()[0];
            }
            if (fieldClass == null) {
                return null;
            }
        } else {
            fieldClass = (Class) fieldType;
        }
        try {
            if (isCollection) {
                paramObj = this.loadListByType(fieldClass);
            } else {
                if (StringUtil.isNotEmpty(name)) {
                    paramObj = this.loadByName(fieldClass, name);
                }
                if (paramObj == null) {
                    paramObj = this.loadByType(fieldClass);
                }
            }
        } catch (Exception ex) {
            log.warn("[{}] Cannot create parameter by type [{}] and name [{}]", LOG_PREFIX,
                    fieldType.getTypeName(), name, ex);
        }
        return paramObj;
    }

    protected void injectByField(Object instance, Field field, String name) {
        Type fieldType = field.getGenericType();
        Object parameter = createParameterObj(fieldType, name);
        if (parameter != null) {
            try {
                ReflectUtil.setFieldValue(instance, field, parameter);
            } catch (UtilException ex) {
                log.warn("[{}] Cannot inject {} by field at {}", LOG_PREFIX,
                        fieldType.getTypeName(), instance.getClass().getName(), ex);
            }
        }
    }

    protected void injectBySetter(Object instance, Method setter) {
        Type paramType = setter.getGenericParameterTypes()[0];
        if (paramType == null) {
            return;
        }
        Object parameter = this.createParameterObj(paramType, null);
        if (parameter != null) {
            try {
                ReflectUtil.invoke(instance, setter, parameter);
            } catch (UtilException ex) {
                log.warn("[{}] Cannot inject {} by {} at {}", LOG_PREFIX,
                        paramType.getTypeName(), setter.getName(), instance.getClass().getName(), ex);
            }
        }
    }
}
