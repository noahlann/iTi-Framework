package org.lan.iti.sdk.pay.configurer.charge;

import org.lan.iti.sdk.pay.configurer.AbstractRequestBuilder;
import org.lan.iti.sdk.pay.model.request.ChargeRequest;

/**
 * @author I'm
 * @since 2021/3/26
 * description 支付参数配置器
 */
public class ChargeRequestBuilder extends AbstractRequestBuilder<ChargeRequest, ChargeRequestBuilder> {

    public ChargeRequestBuilder() {
        super(new ChargeRequest());
    }

    /**
     * 设置支付单号
     *
     * @param outOrderNo 支付单号
     * @return 支付参数配置器
     */
    public ChargeRequestBuilder outOrderNo(String outOrderNo) {
        request.setOutOrderNo(outOrderNo);
        return this;
    }

    /**
     * 设置订单金额
     *
     * @param amount 订单金额
     * @return 支付参数配置器
     */
    public ChargeRequestBuilder amount(Float amount) {
        request.setAmount(amount);
        return this;
    }

    /**
     * 设置订单标题
     *
     * @param subject 订单标题
     * @return 支付参数配置器
     */
    public ChargeRequestBuilder subject(String subject) {
        request.setSubject(subject);
        return this;
    }

    /**
     * 设置订单描述
     *
     * @param body 订单描述
     * @return 支付参数配置器
     */
    public ChargeRequestBuilder body(String body) {
        request.setBody(body);
        return this;
    }

}
