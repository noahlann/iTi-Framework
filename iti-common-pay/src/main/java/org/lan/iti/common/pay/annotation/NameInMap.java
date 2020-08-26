package org.lan.iti.common.pay.annotation;

import java.lang.annotation.*;

/**
 * @author I'm
 * @since 2020/8/25
 * description
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface NameInMap {
    String value();

    String[] alternate() default {};
}
