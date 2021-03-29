package org.lan.iti.common.pay.util;

/**
 * @author I'm
 * @since 2021/3/18
 * description 正则表达式
 */
public interface PatternPool {

    /**
     * 金额
     * */
    String MONEY = "(^[1-9]{1}[0-9]*$)|(^[0-9]*\\.[0-9]{2}$)";

}
