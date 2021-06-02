package org.lan.iti.cloud.axon.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 构造方法注解，解决@Value无法被Jackson正常反序列化的问题
 *
 * @author NorthLan
 * @date 2021-05-24
 * @url https://noahlan.com
 */
@Target(ElementType.CONSTRUCTOR)
@Retention(RetentionPolicy.CLASS)
public @interface Default {
}
