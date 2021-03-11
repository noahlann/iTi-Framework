package org.lan.iti.common.pay.model;

import cn.hutool.core.util.StrUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author I'm
 * @since 2020/9/3
 * description
 */
public class BaseModel {
    ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

    /**
     * 将建构的 builder 转为 Map
     *
     * @return 转化后的 Map
     */
    public ConcurrentHashMap<String, String> toMap() {
        map.clear();
        String[] fieldNames = getFiledNames(this);
        for (String name : fieldNames) {
            String value = (String) getFieldValueByName(name, this);
            if (StrUtil.isNotEmpty(value)) {
                map.put(name, Objects.requireNonNull(value));
            }
        }
        return map;
    }

    /**
     * 获取属性名数组
     *
     * @param obj 对象
     * @return 返回对象属性名数组
     */
    private String[] getFiledNames(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].getName();
        }
        return fieldNames;
    }

    /**
     * 根据属性名获取属性值
     *
     * @param fieldName 属性名称
     * @param obj       对象
     * @return 返回对应属性的值
     */
    private Object getFieldValueByName(String fieldName, Object obj) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" +
                    firstLetter +
                    fieldName.substring(1);
            Method method = obj.getClass().getMethod(getter);
            return method.invoke(obj);
        } catch (Exception e) {
            return null;
        }
    }
}
