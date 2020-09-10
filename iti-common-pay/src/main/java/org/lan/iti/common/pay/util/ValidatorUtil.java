package org.lan.iti.common.pay.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author I'm
 * @since 2020/8/26
 * description 验证工具
 */

@UtilityClass
public class ValidatorUtil {

    private final Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();

    public <T> Map<String, StringBuffer> validateToMap(T obj) {
        Map<String, StringBuffer> errorMap = null;
        Set<ConstraintViolation<T>> set = validator.validate(obj, Default.class);
        if (set != null && set.size() > 0) {
            errorMap = new HashMap<>();
            String property;
            for (ConstraintViolation<T> cv : set) {
                //这里循环获取错误信息，可以自定义格式
                property = cv.getPropertyPath().toString();
                if (errorMap.get(property) != null) {
                    errorMap.get(property).append(",").append(cv.getMessage());
                } else {
                    StringBuffer sb = new StringBuffer();
                    sb.append(cv.getMessage());
                    errorMap.put(property, sb);
                }
            }
        }
        return errorMap;
    }

    public <T> String validateToStr(T obj) {
        StringBuilder errorBuilder = new StringBuilder();
        Set<ConstraintViolation<T>> set = validator.validate(obj, Default.class);
        if (set != null && set.size() > 0) {
            for (ConstraintViolation<T> cv : set) {
                //这里循环获取错误信息，可以自定义格式
                if (StrUtil.isNotBlank(errorBuilder)) {
                    errorBuilder.append("\n").append(cv.getPropertyPath().toString()).append(":").append(cv.getMessage());
                } else {
                    errorBuilder.append(cv.getPropertyPath().toString()).append(":").append(cv.getMessage());
                }
            }
        }
        return Convert.toStr(errorBuilder);
    }

}
