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
import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;


/**
 * 数据校验 工具类
 *
 * @author NorthLan
 * @date 2020-04-18
 * @url https://noahlan.com
 */
@UtilityClass
@Slf4j
public class ValidationUtils {
    /**
     * hibernate注解验证
     */
    private static Validator VALIDATOR = Validation.byProvider(HibernateValidator.class).configure().failFast(false).buildValidatorFactory().getValidator();

    /***
     * 字符串数组是否不为空
     */
    public static boolean isEmpty(String[] values) {
        return values == null || values.length == 0;
    }

    /**
     * 判断是否为数字（允许小数点）
     *
     * @return true Or false
     */
    public static boolean isNumber(String str) {
        String regex = "^(-?[1-9]\\d*\\.?\\d*)|(-?0\\.\\d*[1-9])|(-?[0])|(-?[0]\\.\\d*)$";
        return str.matches(regex);
    }

    /**
     * 判断是否为正确的邮件格式
     *
     * @param str
     * @return boolean
     */
    public static boolean isEmail(String str) {
        if (StrUtil.isEmpty(str)) {
            return false;
        }
        return str.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$");
    }

    /**
     * 是否boolean值范围
     */
    private static final Set<String> TRUE_SET = CollUtil.newHashSet("true", "y", "yes", "1", "是");
    private static final Set<String> FALSE_SET = CollUtil.newHashSet("false", "n", "no", "0", "否");

    /***
     * 是否为boolean类型
     */
    public static boolean isValidBoolean(String value) {
        if (value == null) {
            return false;
        }
        value = value.trim().toLowerCase();
        return TRUE_SET.contains(value) || FALSE_SET.contains(value);
    }

    /***
     * 转换为boolean类型, 并判定是否为true
     */
    public static boolean isTrue(String value) {
        if (value == null) {
            return false;
        }
        value = value.trim().toLowerCase();
        return TRUE_SET.contains(value);
    }
}
