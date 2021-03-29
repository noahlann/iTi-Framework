package org.lan.iti.common.pay.constants;

/**
 * @author I'm
 * @since 2021/3/27
 * description
 */
public interface PayConstants {

    /**
     * 签名
     */
    String SIGN = "sign";

    /**
     * 参数错误
     */
    String PARAM_ERROR = "参数错误";

    /**
     * 渠道编码
     */
    String CHANNEL = "channel";

    /**
     * 最小金额
     */
    String MIN_AMOUNT = "0.01";

    /**
     * 最小金额(支付宝)
     */
    String ALIPAY_MIN_FUND_AMOUNT = "0.1";

    /**
     * 最小金额(微信)
     */
    String WX_MIN_FUND_AMOUNT = "0.3";

    /**
     * 金额比较结果为大于
     */
    int BIG_DECIMAL_GREATER_THAN = 1;

    /**
     * 金额比较结果为等于
     */
    int BIG_DECIMAL_EQUALS = 0;

    /**
     * 金额比较结果为小于
     */
    int BIG_DECIMAL_LESS_THAN = -1;

    /**
     * Http header Content-Type
     * */
    String CONTENT_TYPE = "Content-Type";
}
