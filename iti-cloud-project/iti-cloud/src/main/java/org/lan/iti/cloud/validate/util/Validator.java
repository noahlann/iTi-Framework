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

package org.lan.iti.cloud.validate.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;
import org.lan.iti.common.core.constants.CommonConstants;
import org.lan.iti.cloud.support.SpringContextHolder;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.beanvalidation.LocaleContextMessageInterpolator;
import org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import javax.validation.MessageInterpolator;
import javax.validation.Validation;
import javax.validation.constraints.NotNull;
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
public final class Validator {

    /**
     * hibernate注解验证
     */
    private javax.validation.Validator javaxValidator;
    private org.springframework.validation.Validator springValidator;
    private MessageSource validationMessageSource;

    /**
     * 获取javax.Validator
     *
     * @return 优先获取Bean, 若无法获取, 则取默认属性的Validator
     */
    public javax.validation.Validator getJavaxValidator() {
        if (javaxValidator == null) {
            javaxValidator = SpringContextHolder.getBeanOfNull(javax.validation.Validator.class);
            if (javaxValidator == null) {
                javaxValidator = Validation.byDefaultProvider().configure()
                        .messageInterpolator(new LocaleContextMessageInterpolator(getMessageInterpolator(validationMessageSource)))
                        .buildValidatorFactory()
                        .getValidator();
            }
        }
        return javaxValidator;
    }

    /**
     * 获取spring的validator
     *
     * @return 通过SpringValidatorAdapter的adapter适配
     * @see SpringValidatorAdapter
     */
    public org.springframework.validation.Validator getSpringValidator() {
        javax.validation.Validator javaxValidator = getJavaxValidator();
        if (javaxValidator == null) {
            return null;
        }
        if (springValidator == null) {
            springValidator = new SpringValidatorAdapter(javaxValidator);
        }
        return springValidator;
    }

    /**
     * 获取框架默认的消息解析器
     *
     * @return 框架修订的消息解析器
     */
    public MessageInterpolator getMessageInterpolator(MessageSource messageSource) {
        if (messageSource != null) {
            // 仅第一次调用赋值
            if (validationMessageSource == null) {
                validationMessageSource = messageSource;
            }
            return new ResourceBundleMessageInterpolator(
                    new MessageSourceResourceBundleLocator(messageSource));
        } else {
            return new ResourceBundleMessageInterpolator(
                    new PlatformResourceBundleLocator(CommonConstants.DEFAULT_VALIDATOR_MESSAGES));
        }
    }

    /**
     * 获取仅用于消息验证的 messageSource
     *
     * @return bean-name: ITIConstants.VALIDATION_MESSAGE_SOURCE_BEAN_NAME
     */
    public MessageSource getValidationMessageSource() {
        return validationMessageSource;
    }

    /**
     * 对bean进行参数验证
     *
     * @param target 目标对象
     * @param groups 验证组
     */
    public void validate(Object target, Object... groups) throws BindException {
        DataBinder dataBinder = new DataBinder(target);
        dataBinder.addValidators(getSpringValidator());
        dataBinder.validate(groups);

        // 当存在参数验证错误时,抛出BindException
        BindingResult bindingResult = dataBinder.getBindingResult();
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
    }

    /**
     * 验证拒绝
     *
     * @param target  目标对象
     * @param field   待拒绝字段
     * @param message 消息或消息代码
     */
    public BindException rejectValueException(Object target, String field, String message) {
        BindException bindException = new BindException(target, target.getClass().getSimpleName());
        bindException.rejectValue(field, message, getI18nMessage(message, message));
        return bindException;
    }

    @NotNull
    public String getI18nMessage(@NotNull String code, @NotNull String defaultMessage) {
        String i18nMessage;
        try {
            i18nMessage = getValidationMessageSource().getMessage(code, null, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            // do nothing...
            i18nMessage = defaultMessage;
        }
        return i18nMessage;
    }

    /***
     * 字符串数组是否不为空
     */
    public boolean isEmpty(String[] values) {
        return values == null || values.length == 0;
    }

    /**
     * 是否boolean值范围
     */
    private final Set<String> TRUE_SET = CollUtil.newHashSet("true", "y", "yes", "1", "是");
    private final Set<String> FALSE_SET = CollUtil.newHashSet("false", "n", "no", "0", "否");

    /***
     * 是否为boolean类型
     */
    public boolean isValidBoolean(String value) {
        if (value == null) {
            return false;
        }
        value = value.trim().toLowerCase();
        return TRUE_SET.contains(value) || FALSE_SET.contains(value);
    }

    /***
     * 转换为boolean类型, 并判定是否为true
     */
    public boolean isTrue(String value) {
        if (value == null) {
            return false;
        }
        value = value.trim().toLowerCase();
        return TRUE_SET.contains(value);
    }

    /*
    asserts
     */
    public void assertStateTrue(boolean value, String messageCode, String defaultMessage) {
        if (!value) {
            throw new IllegalStateException(getI18nMessage(messageCode, defaultMessage));
        }
    }

    public void assertStateFalse(boolean value, String messageCode, String defaultMessage) {
        if (value) {
            throw new IllegalStateException(getI18nMessage(messageCode, defaultMessage));
        }
    }

    public void assertArgumentTrue(boolean value, String messageCode, String defaultMessage) {
        if (!value) {
            throw new IllegalArgumentException(getI18nMessage(messageCode, defaultMessage));
        }
    }

    public void assertArgumentRange(long value, long minimum, long maximum, String messageCode, String defaultMessage) {
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(getI18nMessage(messageCode, defaultMessage));
        }
    }

    public void assertArgumentRange(double value, double minimum, double maximum, String messageCode, String defaultMessage) {
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(getI18nMessage(messageCode, defaultMessage));
        }
    }

    public void assertArgumentRange(float value, float minimum, float maximum, String messageCode, String defaultMessage) {
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(getI18nMessage(messageCode, defaultMessage));
        }
    }

    public void assertArgumentRange(int value, int minimum, int maximum, String messageCode, String defaultMessage) {
        if (value < minimum || value > maximum) {
            throw new IllegalArgumentException(getI18nMessage(messageCode, defaultMessage));
        }
    }

    public void assertArgumentEquals(Object object1, Object object2, String messageCode, String defaultMessage) {
        if (!ObjectUtil.equal(object1, object2)) {
            throw new IllegalArgumentException(getI18nMessage(messageCode, defaultMessage));
        }
    }

    public void assertArgumentFalse(boolean bool, String messageCode, String defaultMessage) {
        if (bool) {
            throw new IllegalArgumentException(getI18nMessage(messageCode, defaultMessage));
        }
    }

    public void assertArgumentLength(String str, int maximum, String messageCode, String defaultMessage) {
        int length = str.trim().length();
        if (length > maximum) {
            throw new IllegalArgumentException(getI18nMessage(messageCode, defaultMessage));
        }
    }

    public void assertArgumentLength(String str, int minimum, int maximum, String messageCode, String defaultMessage) {
        int length = str.trim().length();
        if (length < minimum || length > maximum) {
            throw new IllegalArgumentException(getI18nMessage(messageCode, defaultMessage));
        }
    }

    public void assertArgumentNotEmpty(String str, String messageCode, String defaultMessage) {
        if (StrUtil.isEmpty(str)) {
            throw new IllegalArgumentException(getI18nMessage(messageCode, defaultMessage));
        }
    }

    public void assertArgumentNotEquals(Object object1, Object object2, String messageCode, String defaultMessage) {
        if (ObjectUtil.equal(object1, object2)) {
            throw new IllegalArgumentException(getI18nMessage(messageCode, defaultMessage));
        }
    }

    public void assertArgumentNotNull(Object object, String messageCode, String defaultMessage) {
        if (object == null) {
            throw new IllegalArgumentException(getI18nMessage(messageCode, defaultMessage));
        }
    }
}
