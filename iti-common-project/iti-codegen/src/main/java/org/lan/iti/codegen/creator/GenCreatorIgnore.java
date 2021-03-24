package org.lan.iti.codegen.creator;


import java.lang.annotation.*;

/**
 * 忽略@GenCreator自动生成的某字段
 * {@link GenCreator}
 *
 * @author NorthLan
 * @date 2021-02-05
 * @url https://noahlan.com
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Inherited
@Documented
public @interface GenCreatorIgnore {
}
