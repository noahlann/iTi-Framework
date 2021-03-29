package org.lan.iti.sdk.pay.validate;

/**
 * @author I'm
 * @since 2021/3/27
 * description 字段验证接口
 */
public interface Validate {

    /**
     * 获取字段验证器名称
     *
     * @return 字段验证器名称
     */
    String getName();

    /**
     * 匹配字段验证器
     *
     * @param key 字段标识
     * @return 匹配结果
     */
    boolean supports(String key);

    /**
     * 验证给定的值是否为空
     *
     * @param value 待验证的值
     * @return 验证结果
     */
    boolean emptyValidate(Object value);

    /**
     * 验证给定的值是否符合正则表达式
     *
     * @param value 待验证的值
     * @return 验证结果
     */
    boolean patternValidate(Object value);

}
