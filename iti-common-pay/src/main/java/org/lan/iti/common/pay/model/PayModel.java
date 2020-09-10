package org.lan.iti.common.pay.model;

import org.lan.iti.common.pay.annotation.NameInMap;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author I'm
 * @since 2020/8/25
 * description 支付模型
 */
public class PayModel {

    public Map<String, Object> toMap() throws IllegalArgumentException, IllegalAccessException {
        HashMap<String, Object> map = new HashMap();
        Field[] var2 = this.getClass().getFields();

        for (Field field : var2) {
            NameInMap anno = field.getAnnotation(NameInMap.class);
            String key;
            if (anno == null) {
                key = field.getName();
            } else {
                key = anno.value();
            }

            if (null != field.get(this) && List.class.isAssignableFrom(field.get(this).getClass())) {
                ParameterizedType listGenericType = (ParameterizedType) field.getGenericType();
                Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
                Type listActualTypeArgument = listActualTypeArguments[0];
                Class<?> itemType = null;
                if (listActualTypeArgument instanceof Class) {
                    itemType = (Class) listActualTypeArgument;
                }

                ArrayList<Object> arrayField = (ArrayList) field.get(this);
                ArrayList<Object> fieldList = new ArrayList();

                for (Object o : arrayField) {
                    if (null != itemType && PayModel.class.isAssignableFrom(itemType)) {
                        Map<String, Object> fields = ((PayModel) o).toMap();
                        fieldList.add(fields);
                    } else {
                        fieldList.add(o);
                    }
                }

                map.put(key, fieldList);
            } else if (null != field.get(this) && PayModel.class.isAssignableFrom(field.get(this).getClass())) {
                PayModel payModel = (PayModel) field.get(this);
                map.put(key, payModel.toMap());
            } else {
                map.put(key, field.get(this));
            }
        }

        return map;
    }

    public static Map<String, Object> buildMap(PayModel payModel) throws IllegalAccessException {
        return null == payModel ? null : payModel.toMap();
    }
}
