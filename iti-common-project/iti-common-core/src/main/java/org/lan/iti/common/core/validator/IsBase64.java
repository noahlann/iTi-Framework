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

package org.lan.iti.common.core.validator;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import org.lan.iti.common.core.util.PatternPool;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Base64验证
 *
 * @author NorthLan
 * @date 2020-09-16
 * @url https://noahlan.com
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {IsBase64.Base64Validator.class})
public @interface IsBase64 {
    String message() default "{org.lan.constraints.isBase64.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Base64Validator implements ConstraintValidator<IsBase64, String> {

        @Override
        public void initialize(IsBase64 constraintAnnotation) {

        }

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (StrUtil.isBlank(value)) {
                return true;
            }
            return ReUtil.isMatch(PatternPool.PATTERN_BASE64, value);
        }
    }
}
