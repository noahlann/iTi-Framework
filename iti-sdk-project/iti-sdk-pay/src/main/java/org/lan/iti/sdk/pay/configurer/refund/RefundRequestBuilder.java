package org.lan.iti.sdk.pay.configurer.refund;

import org.lan.iti.sdk.pay.configurer.AbstractRequestBuilder;
import org.lan.iti.sdk.pay.model.request.RefundRequest;

import java.util.Map;

/**
 * @author I'm
 * @since 2021/3/26
 * description 退款参数配置器
 */
public class RefundRequestBuilder extends AbstractRequestBuilder<RefundRequest, RefundRequestBuilder> {

    public RefundRequestBuilder() {
        super(new RefundRequest());
    }

    @Override
    public RefundRequestBuilder accept(Map<String, Object> modelParams) {
        return super.accept(modelParams);
    }

    /**
     * 设置支付单号
     *
     * @param outOrderNo 支付单号
     * @return 退款参数配置器
     */
    public RefundRequestBuilder outOrderNo(String outOrderNo) {
        request.setOutOrderNo(outOrderNo);
        return this;
    }

    /**
     * 设置退款单号
     *
     * @param outRefundNo 退款单号
     * @return 退款参数配置器
     */
    public RefundRequestBuilder outRefundNo(String outRefundNo) {
        request.setOutRefundNo(outRefundNo);
        return this;
    }

    /**
     * 设置退款金额
     *
     * @param refundAmount 退款金额
     * @return 退款参数配置器
     */
    public RefundRequestBuilder refundAmount(String refundAmount) {
        request.setRefundAmount(refundAmount);
        return this;
    }

    /**
     * 设置退款描述
     *
     * @param refundDescription 退款描述
     * @return 退款参数配置器
     */
    public RefundRequestBuilder refundDescription(String refundDescription) {
        request.setRefundDescription(refundDescription);
        return this;
    }

    /**
     * 设置退款额外字段
     *
     * @param extra 退款额外字段
     * @return 退款参数配置器
     */
    public RefundRequestBuilder extra(Map<String, Object> extra) {
        request.setExtra(extra);
        return this;
    }

}
