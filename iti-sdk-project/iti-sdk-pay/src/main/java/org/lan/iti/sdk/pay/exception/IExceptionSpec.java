package org.lan.iti.sdk.pay.exception;

/**
 * 顶层异常规范
 *
 * @author NorthLan
 * @date 2021-03-02
 * @url https://noahlan.com
 */
public interface IExceptionSpec {

    /**
     * 获取错误编码
     *
     * @return 规范的错误编码
     */
    String getCode();

    /**
     * 获取错误消息
     * TODO i18n
     *
     * @return 错误信息
     */
    String getMessage();

}
