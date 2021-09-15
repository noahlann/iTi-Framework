package org.lan.iti.common.pay.reason;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lan.iti.common.core.exception.IExceptionSpec;

/**
 * @author I'm
 * @date 2021/4/12
 * description 中台统一定义异常
 */
public interface PaymentExceptionReason {

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    enum Payment implements IExceptionSpec {

        /**
         * 创建支付订单错误原因
         */
        PAYMENT_ORDER_NOT_FOUND("PAYMENT_ORDER_NOT_FOUND", "支付订单不存在"),
        UPDATE_FAIL("UPDATE_FAIL", "支付订单更新失败"),
        EMPTY_APP_ID("EMPTY_APP_ID", "app id不能为空"),
        EMPTY_CHANNEL_CODE("EMPTY_CHANNEL_CODE", "渠道编码不能为空"),
        EMPTY_OUT_ORDER_NO("EMPTY_OUT_ORDER_NO", "订单号不能为空"),
        EMPTY_SUBJECT("EMPTY_SUBJECT", "订单标题不能为空"),
        EMPTY_AMOUNT("EMPTY_AMOUNT", "金额不能为空"),
        PARSE_PARAM_ERROR("PARSE_PARAM_ERROR", "参数解析错误"),
        INVALID_MIN_AMOUNT("INVALID_MIN_AMOUNT", "金额必须大于0.01元"),
        PAID("PAID", "该订单已支付,请勿重复请求"),
        REFUND_AMOUNT_NOT_AVAILABLE("REFUND_AMOUNT_NOT_AVAILABLE", "无可用退款金额"),
        ORDER_TIME_OUT("ORDER_TIME_OUT", "订单超时"),
        INVALID_ALIPAY_TRANSFER_MIN_AMOUNT("INVALID_ALIPAY_TRANSFER_MIN_AMOUNT", "金额必须大于0.1元"),
        INVALID_WX_TRANSFER_MIN_AMOUNT("INVALID_WX_TRANSFER_MIN_AMOUNT", "金额必须大于0.3元"),
        CHANNEL_CODE_EMPTY("CHANNEL_CODE_EMPTY", "渠道编码不能为空"),
        METHOD_TYPE_NOT_FOUND("METHOD_TYPE_NOT_FOUND", "请求类型未找到"),
        ;


        private final String code;
        private final String message;

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    enum Refund implements IExceptionSpec {

        /**
         * 退款错误原因
         */
        REFUND_AMOUNT_NOT_AVAILABLE("REFUND_AMOUNT_NOT_AVAILABLE", "无可用退款金额"),
        PAYMENT_ORDER_NOT_PAID("PAYMENT_ORDER_NOT_PAID", "该订单未支付"),
        REFUND_ORDER_NOT_PAID("REFUND_ORDER_NOT_PAID", "该退款订单不存在"),
        REFUNDED("REFUNDED", "该订单已退款,请勿重复请求"),
        REFUND_ORDER_NOT_FOUND("NOT_FOUND", "退款单号不存在"),
        ;


        private final String code;
        private final String message;

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    enum Transfer implements IExceptionSpec {

        /**
         * 退款错误原因
         */
        PARSE_PARAM_ERROR("PARSE_PARAM_ERROR", "参数解析异常"),
        RESPONSE_PARAM_ERROR_WX_MCH_ID_APPID("RESPONSE_PARAM_ERROR_WX_MCH_ID_APPID", "微信回传参数：app id 、 mch id 与平台参数不一致：请重新发起请求！"),
        UPACP_NOT_ONLINE("UPACP_NOT_ONLINE", "银联渠道暂未开通转账！"),
        TO_BANK_NOT_AVAILABLE("TO_BANK_NOT_AVAILABLE", "企业付款到银行卡产品尚未开通！"),
        TRANSFERRED("TRANSFERRED", "该订单已转账,请勿重复请求！"),
        BIZ_ERROR("BIZ_ERROR", "业务异常！"),
        NOT_FOUND("NOT_FOUND", "退款单号不存在！"),
        ;


        private final String code;
        private final String message;

    }

}
