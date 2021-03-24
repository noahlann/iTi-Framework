package org.lan.iti.codegen.annotation;

import java.lang.annotation.*;

/**
 * 删除方法
 *
 * @author NorthLan
 * @date 2021-02-01
 * @url https://noahlan.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
@Inherited
public @interface DeleteMethod {
}
