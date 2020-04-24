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

package org.lan.iti.common.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

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
public class BeanUtils extends org.springframework.beans.BeanUtils {

    /**
     * 连接符号
     */
    private static final String CHANGE_FLAG = "->";

    /**
     * 忽略对比的字段
     */
    private static final Set<String> IGNORE_FIELDS = CollUtil.newHashSet("createTime");


    /***
     * 将对象转换为另外的对象实例
     * @param source 源对象
     * @param clazz 目标对象类
     * @param <T> 泛型
     */
    public static <T> T convert(Object source, Class<T> clazz) {
        if (source == null) {
            return null;
        }
        T target = null;
        try {
            target = clazz.getConstructor().newInstance();
            copyProperties(source, target);
        } catch (Exception e) {
            log.warn("对象转换异常, class=" + clazz.getName());
        }
        return target;
    }

    /***
     * 将对象转换为另外的对象实例
     * @param sourceList 源对象列表
     * @param clazz 目标对象类型
     * @param <T> 对象泛型
     */
    public static <T> List<T> convertList(List<?> sourceList, Class<T> clazz) {
        if (CollectionUtils.isEmpty(sourceList)) {
            return Collections.emptyList();
        }
        List<T> resultList = new ArrayList<>();
        // 类型相同，直接跳过
        if (clazz.getName().equals(sourceList.get(0).getClass().getName())) {
            return (List<T>) sourceList;
        }
        // 不同，则转换
        try {
            for (Object source : sourceList) {
                T target = clazz.getConstructor().newInstance();
                copyProperties(source, target);
                resultList.add(target);
            }
        } catch (Exception e) {
            log.warn("对象转换异常, class: {}, error: {}", clazz.getName(), e.getMessage());
        }
        return resultList;
    }

//    /***
//     * 附加Map中的属性值到Model
//     */
//    public static void bindProperties(Object model, Map<String, Object> propMap) {
//        if (CollectionUtils.isEmpty(propMap)) {
//            return;
//        }
//        List<Field> fields = extractAllFields(model.getClass());
//        Map<String, Field> fieldNameMaps = convertToStringKeyObjectMap(fields, "name");
//        for (Map.Entry<String, Object> entry : propMap.entrySet()) {
//            Field field = fieldNameMaps.get(entry.getKey());
//            if (field != null) {
//                try {
//                    Object value = convertValueToFieldType(entry.getValue(), field);
//                    setProperty(model, entry.getKey(), value);
//                } catch (Exception e) {
//                    log.warn("复制属性{}.{}异常: {}", model.getClass().getSimpleName(), entry.getKey(), e.getMessage());
//                }
//            }
//        }
//    }

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
            return ValidationUtils.isTrue(StringUtils.valueOf(value));
        } else if (type.contains(Date.class.getSimpleName())) {
            return DateUtils.fuzzyConvert(StringUtils.valueOf(value));
        }
        return value;
    }

    /***
     * Key-Object对象Map
     */
    public static <T> Map<String, T> convertToStringKeyObjectMap(List<T> allLists, String... fields) {
        if (allLists == null || allLists.isEmpty()) {
            return Collections.EMPTY_MAP;
        }
        Map<String, T> allListMap = new LinkedHashMap<>(allLists.size());
        // 转换为map
        try {
            for (T model : allLists) {
                String key = null;
                if (ValidationUtils.isEmpty(fields)) {
                    //未指定字段，以id为key
                    key = getStringProperty(model, "id");
                }
                // 指定了一个字段，以该字段为key，类型同该字段
                else if (fields.length == 1) {
                    key = getStringProperty(model, fields[0]);
                } else { // 指定了多个字段，以字段S.join的结果为key，类型为String
                    List<Object> list = new ArrayList<>();
                    for (String fld : fields) {
                        list.add(getProperty(model, fld));
                    }
                    key = StrUtil.join(",", list);
                }
                if (key != null) {
                    allListMap.put(key, model);
                } else {
                    log.warn(model.getClass().getName() + " 的属性 " + fields[0] + " 值存在 null，转换结果需要确认!");
                }
            }
        } catch (Exception e) {
            log.warn("转换key-model异常", e);
        }
        return allListMap;
    }

    /***
     * Key-Object-List列表Map
     * @param allLists
     * @param fields
     * @param <T>
     * @return
     */
    public static <T> Map<String, List<T>> convertToStringKeyObjectListMap(List<T> allLists, String... fields) {
        if (allLists == null || allLists.isEmpty()) {
            return null;
        }
        Map<String, List<T>> allListMap = new LinkedHashMap<>(allLists.size());
        // 转换为map
        try {
            for (T model : allLists) {
                String key = null;
                if (ValidationUtils.isEmpty(fields)) {
                    //未指定字段，以id为key
                    key = getStringProperty(model, "id");
                }
                // 指定了一个字段，以该字段为key，类型同该字段
                else if (fields.length == 1) {
                    key = getStringProperty(model, fields[0]);
                } else { // 指定了多个字段，以字段S.join的结果为key，类型为String
                    List<Object> list = new ArrayList<>();
                    for (String fld : fields) {
                        list.add(getProperty(model, fld));
                    }
                    key = StrUtil.join(",", list);
                }
                if (key != null) {
                    List<T> list = allListMap.computeIfAbsent(key, k -> new ArrayList<>());
                    list.add(model);
                } else {
                    log.warn(model.getClass().getName() + " 的属性 " + fields[0] + " 值存在 null，转换结果需要确认!");
                }
            }
        } catch (Exception e) {
            log.warn("转换key-model-list异常", e);
        }
        return allListMap;
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
