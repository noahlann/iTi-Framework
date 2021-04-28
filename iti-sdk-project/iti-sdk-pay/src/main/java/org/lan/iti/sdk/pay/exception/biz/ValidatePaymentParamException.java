package org.lan.iti.sdk.pay.exception.biz;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lan.iti.sdk.pay.exception.IExceptionSpec;

/**
 * @author I'm
 * @date 2021/4/28
 * description 中台统一定义参数验证异常
 */
public interface ValidatePaymentParamException {

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    enum GatewayHostException implements IExceptionSpec {

        /**
         * 渠道信息配置错误原因
         */
        EMPTY_GATEWAY_HOST("10100", "接口网关(gatewayHost)不能为空"),
        INVALID_GATEWAY_HOST_PATTERN("10101", "接口网关(gatewayHost)格式有误");

        private final String code;
        private final String message;

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    enum OutOrderNoException implements IExceptionSpec {

        /**
         * 渠道信息配置错误原因
         */
        EMPTY_OUT_ORDER_NO("20100", "商户支付单号(outOrderNo)不能为空");

        private final String code;
        private final String message;

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    enum AmountException implements IExceptionSpec {

        /**
         * 渠道信息配置错误原因
         */
        EMPTY_AMOUNT("30100", "金额(amount)不能为空"),
        INVALID_AMOUNT("30101", "金额(amount)格式有误");

        private final String code;
        private final String message;

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    enum SubjectException implements IExceptionSpec {

        /**
         * 渠道信息配置错误原因
         */
        EMPTY_SUBJECT("40100", "订单标题(subject)不能为空");

        private final String code;
        private final String message;

    }


}