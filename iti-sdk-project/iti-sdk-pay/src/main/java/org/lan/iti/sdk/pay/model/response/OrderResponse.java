package org.lan.iti.sdk.pay.model.response;

import lombok.Data;
import org.lan.iti.sdk.pay.model.IResponse;

import java.util.Map;

/**
 * @author I'm
 * @since 2021/3/26
 * description 支付业务统一响应参数
 */
@Data
public class OrderResponse implements IResponse {

    /**
     * 平台 app id
     */
    private String appId;

    /**
     * 订单额外字段
     */
    private Map<String, Object> extra;

    /**
     * 支付状态
     */
    private String status;

    /**
     * 退款状态
     */
    private String refundStatus;

    /**
     * 支付渠道
     */
    private String channel;

    /**
     * 商户支付单号
     */
    private String outOrderNo;

    /**
     * 平台支付单号
     */
    private String orderNo;

    /**
     * 订单标题
     */
    private String subject;

    /**
     * 订单描述
     */
    private String body;

    /**
     * 订单创建时间
     */
    private String createTime;

    /**
     * 支付时间
     */
    private String paidTime;

    /**
     * 超时时间
     */
    private String expiredTime;

    /**
     * 渠道支付单号
     */
    private String transactionNo;

    /**
     * 退款金额
     */
    private String refundAmount;

    /**
     * 退款描述(暂无退款;部分退款;全部退款)
     */
    private String refundDescription;

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 描述
     */
    private String description;

    /**
     * wap url
     */
    private String wapUrl;

}
