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
    String APP_ID = "app_id";

    /**
     * privateKey 私钥
     */
    String PRIVATE_KEY = "private_key";

    /**
     * privateKey 接口网关
     */
    String GATEWAY_HOST = "gateway_host";

    /**
     * outOrderNo 商户订单号
     */
    String OUT_ORDER_NO = "out_order_no";

    /**
     * outRefundNo 商户退款订单号
     */
    String OUT_REFUND_NO = "out_refund_no";

    /**
     * outFundNo 商户转账订单号
     */
    String OUT_FUND_NO = "out_fund_no";

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
    String REFUND_AMOUNT = "refund_amount";

    /**
     * 转账金额
     */
    String FUND_AMOUNT = "fund_amount";

    /**
     * 转账到账用户类型
     */
    String ACCOUNT_ID = "account_id";

    /**
     * 转账到账用户标识
     */
    String ACCOUNT_TYPE = "account_type";

    /**
     * 收款方姓名
     */
    String ACCOUNT_NAME = "account_name";

    /**
     * 转账渠道
     */
    String FUND_CHANNEL = "fund_channel";

    /**
     * 银行编号
     */
    String BANK_CODE = "bank_code";

    /**
     * 是否转账到银行卡（Y:是；N:否）
     */
    String TO_BANK = "to_bank";

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
    String AUTH_CODE = "auth_code";

    /**
     * 签名
     */
    String SIGN = "sign";

}
