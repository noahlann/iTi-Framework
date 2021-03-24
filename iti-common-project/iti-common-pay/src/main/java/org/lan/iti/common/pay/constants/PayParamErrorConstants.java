package org.lan.iti.common.pay.constants;

/**
 * @author I'm
 * @since 2021/3/17
 * description 参数错误的提示
 */
public interface PayParamErrorConstants {
    /**
     * 接口网关(gatewayHost)为空
     */
    String GATEWAY_HOST_EMPTY = "接口网关(gatewayHost)不能为空,请检查！";
    /**
     * 接口网关(gatewayHost)格式有误
     */
    String GATEWAY_HOST_PATTERN = "接口网关(gatewayHost)格式有误,请检查！";
    /**
     * 订单号为空
     */
    String OUT_ORDER_EMPTY = "订单号不能为空!";
    /**
     * 退款订单号为空
     */
    String OUT_REFUND_EMPTY = "退款订单号不能为空!";
    /**
     * 转账订单号为空
     */
    String OUT_FUND_EMPTY = "转账订单号不能为空!";
    /**
     * 金额为空
     */
    String AMOUNT_EMPTY = "金额不能为空!";
    /**
     * 金额为空
     */
    String AMOUNT_PATTERN = "金额有误!";
    /**
     * 订单标题为空
     */
    String SUBJECT_EMPTY = "订单标题不为空!";
    /**
     * 转账到账用户标识为空
     */
    String ACCOUNT_ID_EMPTY = "转账到账用户标识不能为空!";
    /**
     * 转账到账用户类型为空
     */
    String ACCOUNT_TYPE_EMPTY = "转账到账用户类型不能为空!";
    /**
     * 收款方姓名为空
     */
    String ACCOUNT_NAME_EMPTY = "收款方姓名不能为空!";
    /**
     * 是否转账到银行卡（Y:是；N:否）为空
     */
    String TO_BANK_EMPTY = "缺少'是否转账到银行卡'字段!";
    /**
     * 是否转账到银行卡（Y:是；N:否） 不为 Y 或 N
     */
    String TO_BANK_PATTERN = "是否转账到银行卡值请填写为'Y'或'N'";
    /**
     * 渠道授权码为空
     */
    String AUTH_CODE_EMPTY = "渠道授权码不能为空!";
    /**
     * 渠道为空
     */
    String CHANNEL_EMPTY = "渠道不能为空!";
    /**
     * 渠道为空
     */
    String FUND_CHANNEL_EMPTY = "渠道不能为空!";
    /**
     * 渠道填写错误
     */
    String FUND_CHANNEL_ERROR = "渠道填写错误!";


}
