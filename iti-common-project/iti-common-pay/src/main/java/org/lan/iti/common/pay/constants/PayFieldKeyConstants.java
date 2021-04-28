package org.lan.iti.common.pay.constants;

/**
 * @author I'm
 * @since 2021/3/29
 * description 支付字段名称
 */
public interface PayFieldKeyConstants {

    /**
     * 平台 app id
     */
    String APP_ID = "appId";

    /**
     * privateKey 私钥
     */
    String PRIVATE_KEY = "privateKey";

    /**
     * privateKey 接口网关
     */
    String GATEWAY_HOST = "gatewayHost";

    /**
     * outOrderNo 商户订单号
     */
    String OUT_ORDER_NO = "outOrderNo";

    /**
     * outRefundNo 商户退款订单号
     */
    String OUT_REFUND_NO = "outRefundNo";

    /**
     * outFundNo 商户转账订单号
     */
    String OUT_FUND_NO = "outFundNo";

    /**
     * 订单标题
     */
    String SUBJECT = "subject";

    /**
     * 订单金额
     */
    String AMOUNT = "amount";

    /**
     * 退款金额
     */
    String REFUND_AMOUNT = "refundAmount";

    /**
     * 转账金额
     */
    String FUND_AMOUNT = "fundAmount";

    /**
     * 转账到账用户类型
     */
    String ACCOUNT_ID = "accountId";

    /**
     * 转账到账用户标识
     */
    String ACCOUNT_TYPE = "accountType";

    /**
     * 收款方姓名
     */
    String ACCOUNT_NAME = "accountName";

    /**
     * 转账渠道
     */
    String FUND_CHANNEL = "fundChannel";

    /**
     * 银行编号
     */
    String BANK_CODE = "bankCode";

    /**
     * 是否转账到银行卡（Y:是；N:否）
     */
    String TO_BANK = "toBank";

    /**
     * 是否转账到银行卡（Y:是；N:否） 是
     */
    String TO_BANK_Y = "Y";

    /**
     * 是否转账到银行卡（Y:是；N:否） 否
     */
    String TO_BANK_N = "N";

    /**
     * 授权码
     */
    String AUTH_CODE = "authCode";

    /**
     * 签名
     */
    String SIGN = "sign";

}
