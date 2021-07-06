package org.lan.iti.sdk.pay.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lan.iti.sdk.pay.model.IResponse;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author I'm
 * @since 2021/3/27
 * description 退款业务统一响应参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundResponse implements IResponse {

    /**
     * 平台 app id
     */
    private String appId;

    /**
     * 平台支付单号
     */
    private String outOrderNo;

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
    private String transactionNo;

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
    private LocalDateTime refundedTime;

    /**
     * 退款订单详情
     */
    private String description;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 退款额外字段
     */
    private Map<String, Object> extra;

    /**
     * 退款额外信息(渠道退款通知信息)
     */
    private Map<String, Object> refundExtra;

}
