package org.lan.iti.sdk.pay.model.response;

import lombok.Getter;
import org.lan.iti.sdk.pay.model.IResponse;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author I'm
 * @since 2021/3/27
 * description 退款业务统一响应参数
 */
@Getter
public class RefundResponse implements IResponse {

    /**
     * 平台 app id
     */
    private String appId;

    /**
     * 退款额外字段
     */
    private Map<String, Object> extra;

    /**
     * 平台支付单号
     */
    private String chargeOrderNo;

    /**
     * 商户退款单号
     */
    private String outRefundNo;

    /**
     * 平台退款单号
     */
    private String refundNo;

    /**
     * 渠道退款流水号
     */
    private String refundTransactionNo;

    /**
     * 退款金额 数值大于0
     */
    private String refundAmount;

    /**
     * 退款开始时间
     */
    private LocalDateTime createTime;

    /**
     * 退款状态 处理中:refunding 成功:refunded 失败:failed 状态未知:unknown
     */
    private String status;

    /**
     * 退款成功时间
     */
    private LocalDateTime refundTime;

    /**
     * 退款订单详情
     */
    private String description;

    /**
     * 错误码
     */
    private String errCode;

    /**
     * 错误消息
     */
    private String errMsg;

}
