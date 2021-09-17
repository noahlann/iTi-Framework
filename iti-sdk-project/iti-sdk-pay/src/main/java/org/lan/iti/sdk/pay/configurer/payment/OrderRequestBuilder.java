package org.lan.iti.sdk.pay.configurer.payment;

import org.lan.iti.sdk.pay.configurer.AbstractRequestBuilder;
import org.lan.iti.sdk.pay.model.request.OrderRequest;

import java.util.Map;

/**
 * @author I'm
 * @since 2021/3/26
 * description 支付参数配置器
 */
public class OrderRequestBuilder extends AbstractRequestBuilder<OrderRequest, OrderRequestBuilder> {

    public OrderRequestBuilder() {
        super(new OrderRequest());
    }

    /**
     * 设置支付单号
     *
     * @param outOrderNo 支付单号
     * @return 支付参数配置器
     */
    public OrderRequestBuilder outOrderNo(String outOrderNo) {
        request.setOutOrderNo(outOrderNo);
        return this;
    }

    /**
     * 设置订单金额
     *
     * @param amount 订单金额
     * @return 支付参数配置器
     */
    public OrderRequestBuilder amount(String amount) {
        request.setAmount(amount);
        return this;
    }

    /**
     * 设置订单标题
     *
     * @param subject 订单标题
     * @return 支付参数配置器
     */
    public OrderRequestBuilder subject(String subject) {
        request.setSubject(subject);
        return this;
    }

    /**
     * 设置订单描述
     *
     * @param description 订单描述
     * @return 支付参数配置器
     */
    public OrderRequestBuilder description(String description) {
        request.setDescription(description);
        return this;
    }

    /**
     * 设置订单额外字段
     *
     * @param extra 订单描述
     * @return 支付参数配置器
     */
    public OrderRequestBuilder extra(Map<String, Object> extra) {
        request.setExtra(extra);
        return this;
    }

}
