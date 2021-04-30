package org.lan.iti.sdk.pay.configurer.payment;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import org.lan.iti.common.pay.constants.PayConstants;
import org.lan.iti.common.pay.util.PatternPool;
import org.lan.iti.sdk.pay.configurer.AbstractRequestBuilder;
import org.lan.iti.sdk.pay.exception.BusinessException;
import org.lan.iti.sdk.pay.exception.biz.ValidatePaymentParamException;
import org.lan.iti.sdk.pay.model.request.OrderRequest;

import java.math.BigDecimal;

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
     * @param body 订单描述
     * @return 支付参数配置器
     */
    public OrderRequestBuilder body(String body) {
        request.setBody(body);
        return this;
    }

    public void validate() {
        if (StrUtil.isBlank(request.getOutOrderNo())) {
            throw new BusinessException(ValidatePaymentParamException.OutOrderNoException.EMPTY_OUT_ORDER_NO);
        }
        if (StrUtil.isBlank(request.getSubject())) {
            throw new BusinessException(ValidatePaymentParamException.SubjectException.EMPTY_SUBJECT);
        }
        validateAmount(request.getAmount());
    }

    private void validateAmount(String amount) {
        if (StrUtil.isBlankIfStr(request.getAmount())) {
            throw new BusinessException(ValidatePaymentParamException.AmountException.EMPTY_AMOUNT);
        }
        BigDecimal amountBigDecimal = new BigDecimal(amount);
        BigDecimal bizAmountBigDecimal = new BigDecimal(PayConstants.MIN_AMOUNT);
        if (!ReUtil.isMatch(PatternPool.MONEY, amount) || amountBigDecimal.compareTo(bizAmountBigDecimal) < PayConstants.BIG_DECIMAL_EQUALS) {
            throw new BusinessException(ValidatePaymentParamException.AmountException.EMPTY_AMOUNT);
        }
    }

}
