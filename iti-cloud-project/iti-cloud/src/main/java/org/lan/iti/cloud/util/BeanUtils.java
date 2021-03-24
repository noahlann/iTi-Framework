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

package org.lan.iti.cloud.util;

import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.cloud.validate.util.Validator;
import org.lan.iti.common.core.util.DateUtils;
import org.lan.iti.common.core.util.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.util.CollectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Bean 工具类
 * <p>
 * 扩展 spring 功能
 * </p>
 *
 * @author NorthLan
 * @date 2020-04-18
 * @url https://noahlan.com
 */
@SuppressWarnings("unchecked")
@Slf4j
public class BeanUtils {
    private static final Map<AbstractMap.SimpleEntry<Class<?>, Class<?>>, BeanCopier> COPIER_MAP = new ConcurrentHashMap<>();

    @Deprecated
    private static BeanCopier getBeanCopier(Class<?> source, Class<?> target, boolean useConvert) {
        if (Objects.isNull(source) || Objects.isNull(target)) {
            throw new IllegalArgumentException();
        }
        AbstractMap.SimpleEntry<Class<?>, Class<?>> key = new AbstractMap.SimpleEntry<>(source, target);
        return COPIER_MAP.computeIfAbsent(key, k -> BeanCopier.create(k.getKey(), k.getValue(), useConvert));
    }

    public static void copyPropertiesNonNull(Object source, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrapper = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = wrapper.getPropertyDescriptors();
        Set<String> nullProperties = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = wrapper.getPropertyValue(pd.getName());
            if (srcValue == null) {
                nullProperties.add(pd.getName());
            }
        }
        return nullProperties.toArray(new String[]{});
    }

    public static void copyProperties(Object source, Object target) {
        // getBeanCopier(source.getClass(), target.getClass(), false).copy(source, target, null);
        // 因为使用了链式调用,BeanCopier无法复制属性(cglib使用方法签名),在写出更好的方法之前,暂时使用Spring BeanUtils
        org.springframework.beans.BeanUtils.copyProperties(source, target);
    }

    /***
     * 将对象转换为另外的对象实例
     * @param source 源对象
     * @param target 目标对象类
     * @param <T> 泛型
     */
    public static <T> T convert(Object source, Class<T> target) {
        if (source == null) {
            return null;
        }
        T result = null;
        try {
            result = target.getConstructor().newInstance();
            copyProperties(source, result);
        } catch (Exception e) {
            log.warn("对象转换异常, class=" + target.getName());
        }
        return result;
    }

    /***
     * 将对象转换为另外的对象实例
     * @param sourceList 源对象列表
     * @param target 目标对象类型
     * @param <T> 对象泛型
     */
    public static <T> List<T> convertList(List<?> sourceList, Class<T> target) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return Collections.emptyList();
        }
        return Optional.of(sourceList)
                .orElse(new ArrayList<>())
                .stream().map(m -> convert(m, target))
                .collect(Collectors.toList());
    }

    /***
     * 获取对象的属性值
     */
    public static Object getProperty(Object obj, String field) {
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(obj);
        return wrapper.getPropertyValue(field);
    }

    /***
     * 获取对象的属性值并转换为String
     */
    public static String getStringProperty(Object obj, String field) {
        Object property = getProperty(obj, field);
        if (property == null) {
            return null;
        }
        return String.valueOf(property);
    }

    /***
     * 设置属性值
     */
    public static void setProperty(Object obj, String field, Object value) {
        BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(obj);
        wrapper.setPropertyValue(field, value);
    }

    /**
     * 转换为field对应的类型
     */
    public static Object convertValueToFieldType(Object value, Field field) {
        String type = field.getGenericType().getTypeName();
        if (value.getClass().getName().equals(type)) {
            return value;
        }
        if (Integer.class.getName().equals(type)) {
            return Integer.parseInt(StringUtils.valueOf(value));
        } else if (Long.class.getName().equals(type)) {
            return Long.parseLong(StringUtils.valueOf(value));
        } else if (Double.class.getName().equals(type)) {
            return Double.parseDouble(StringUtils.valueOf(value));
        } else if (BigDecimal.class.getName().equals(type)) {
            return new BigDecimal(StringUtils.valueOf(value));
        } else if (Float.class.getName().equals(type)) {
            return Float.parseFloat(StringUtils.valueOf(value));
        } else if (Boolean.class.getName().equals(type)) {
            return Validator.isTrue(StringUtils.valueOf(value));
        } else if (type.contains(Date.class.getSimpleName())) {
            return DateUtils.fuzzyConvert(StringUtils.valueOf(value));
        }
        return value;
    }

    /**
     * 获取类所有属性（包含父类中属性）
     */
    public static List<Field> extractAllFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        Set<String> fieldNameSet = new HashSet<>();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            if (ArrayUtil.isNotEmpty(fields)) { //被重写属性，以子类override的为准
                Arrays.stream(fields).forEach((field) -> {
                    if (!fieldNameSet.contains(field.getName())) {
                        fieldList.add(field);
                        fieldNameSet.add(field.getName());
                    }
                });
            }
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }
}
