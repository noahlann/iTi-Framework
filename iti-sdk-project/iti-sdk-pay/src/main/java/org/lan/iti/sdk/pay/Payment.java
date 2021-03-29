package org.lan.iti.sdk.pay;

import lombok.experimental.UtilityClass;
import org.lan.iti.sdk.pay.configurer.AbstractRequestBuilder;
import org.lan.iti.sdk.pay.configurer.charge.ChargeRequestBuilder;
import org.lan.iti.sdk.pay.configurer.fund.FundRequestBuilder;
import org.lan.iti.sdk.pay.configurer.refund.RefundRequestBuilder;
import org.lan.iti.sdk.pay.model.DefaultResponse;
import org.lan.iti.sdk.pay.model.IRequest;
import org.lan.iti.sdk.pay.model.request.ChargeRequest;
import org.lan.iti.sdk.pay.model.request.FundRequest;
import org.lan.iti.sdk.pay.model.request.RefundRequest;
import org.lan.iti.sdk.pay.model.response.ChargeResponse;
import org.lan.iti.sdk.pay.model.response.FundResponse;
import org.lan.iti.sdk.pay.model.response.RefundResponse;
import org.lan.iti.sdk.pay.payment.DefaultPayment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author I'm
 * @since 2021/3/26
 * description 支付相关业务
 */
@UtilityClass
public class Payment {

    private static final Map<Class<? extends IRequest>, AbstractRequestBuilder<? extends IRequest, ? extends AbstractRequestBuilder<?, ?>>> DEFAULT_PAYMENT_MAP = new HashMap<>();

    static {
        DEFAULT_PAYMENT_MAP.put(ChargeRequest.class, new ChargeRequestBuilder());
        DEFAULT_PAYMENT_MAP.put(RefundRequest.class, new RefundRequestBuilder());
        DEFAULT_PAYMENT_MAP.put(FundRequest.class, new FundRequestBuilder());
    }

    @SuppressWarnings("unchecked")
    private static <T extends IRequest, R extends AbstractRequestBuilder<T, R>> R configurer(Class<T> clazz) {
        return (R) Optional.ofNullable(DEFAULT_PAYMENT_MAP.get(clazz))
                .orElseThrow(() -> new IllegalArgumentException("给定参数类型不存在"));
    }

    /**
     * 支付参数配置
     *
     * @return 支付参数配置器
     */
    public static ChargeRequestBuilder chargeConfigurer() {
        return configurer(ChargeRequest.class);
    }

    /**
     * 支付参数配置
     *
     * @param chargeParamMap 支付数据(map结构)
     * @return 支付参数配置器
     */
    public static ChargeRequestBuilder chargeConfigurer(Map<String, Object> chargeParamMap) {
        ChargeRequestBuilder configurer = configurer(ChargeRequest.class);
        configurer.accept(chargeParamMap);
        return configurer;
    }

    /**
     * 退款参数配置
     *
     * @return 退款参数配置器
     */
    public static RefundRequestBuilder refundConfigurer() {
        return configurer(RefundRequest.class);
    }

    /**
     * 退款参数配置
     *
     * @param refundParamMap 退款数据(map结构)
     * @return 退款参数配置器
     */
    public static RefundRequestBuilder refundConfigurer(Map<String, Object> refundParamMap) {
        RefundRequestBuilder configurer = configurer(RefundRequest.class);
        configurer.accept(refundParamMap);
        return configurer;
    }

    /**
     * 转账参数配置
     *
     * @return 转账参数配置器
     */
    public static FundRequestBuilder fundConfigurer() {
        return configurer(FundRequest.class);
    }

    /**
     * 转账参数配置
     *
     * @param fundParamMap 转账数据(map结构)
     * @return 转账参数配置器
     */
    public static FundRequestBuilder fundConfigurer(Map<String, Object> fundParamMap) {
        FundRequestBuilder configurer = configurer(FundRequest.class);
        configurer.accept(fundParamMap);
        return configurer;
    }

    /**
     * 创建订单
     *
     * @param chargeRequestBuilder 支付参数配置器
     * @return 支付业务响应统一结构
     */
    public static ChargeResponse charge(ChargeRequestBuilder chargeRequestBuilder) {
        ChargeRequest chargeRequest = chargeRequestBuilder.build();
        DefaultResponse<ChargeResponse> defaultResponse = new DefaultPayment<ChargeRequest, ChargeResponse>(chargeRequest).execute();
        return defaultResponse.getResponse();
    }

    /**
     * 创建订单(批量)
     *
     * @param chargeRequestBuilder 支付参数配置器
     * @return 支付业务响应统一结构
     */
    public static ChargeResponse chargeBatch(ChargeRequestBuilder chargeRequestBuilder) {
        ChargeRequest chargeRequest = chargeRequestBuilder.build();
        DefaultResponse<ChargeResponse> defaultResponse = new DefaultPayment<ChargeRequest, ChargeResponse>(chargeRequest).execute();
        return defaultResponse.getResponse();
    }

    /**
     * 订单查询
     *
     * @param chargeRequestBuilder 支付参数配置器
     * @return 支付业务响应统一结构
     */
    public static ChargeResponse chargeQuery(ChargeRequestBuilder chargeRequestBuilder) {
        ChargeRequest chargeRequest = chargeRequestBuilder.build();
        DefaultResponse<ChargeResponse> defaultResponse = new DefaultPayment<ChargeRequest, ChargeResponse>(chargeRequest).execute();
        return defaultResponse.getResponse();
    }

    /**
     * 订单查询(批量)
     *
     * @param chargeRequestBuilder 支付参数配置器
     * @return 支付业务响应统一结构
     */
    public static List<ChargeResponse> chargeList(ChargeRequestBuilder chargeRequestBuilder) {
        ChargeRequest chargeRequest = chargeRequestBuilder.build();
        DefaultResponse<ChargeResponse> defaultResponse = new DefaultPayment<ChargeRequest, ChargeResponse>(chargeRequest).execute();
        return defaultResponse.getResponseList();
    }

    /**
     * 申请退款
     *
     * @param refundRequestBuilder 退款参数配置器
     * @return 退款业务响应统一结构
     */
    public static RefundResponse refund(RefundRequestBuilder refundRequestBuilder) {
        RefundRequest refundRequest = refundRequestBuilder.build();
        DefaultResponse<RefundResponse> defaultResponse = new DefaultPayment<RefundRequest, RefundResponse>(refundRequest).execute();
        return defaultResponse.getResponse();
    }

    /**
     * 申请退款
     *
     * @param refundRequestBuilder 退款参数配置器
     * @return 退款业务响应统一结构
     */
    public static RefundResponse refundBatch(RefundRequestBuilder refundRequestBuilder) {
        RefundRequest refundRequest = refundRequestBuilder.build();
        DefaultResponse<RefundResponse> defaultResponse = new DefaultPayment<RefundRequest, RefundResponse>(refundRequest).execute();
        return defaultResponse.getResponse();
    }

    /**
     * 退款查询
     *
     * @param refundRequestBuilder 退款参数配置器
     * @return 退款业务响应统一结构
     */
    public static RefundResponse refundQuery(RefundRequestBuilder refundRequestBuilder) {
        RefundRequest refundRequest = refundRequestBuilder.build();
        DefaultResponse<RefundResponse> defaultResponse = new DefaultPayment<RefundRequest, RefundResponse>(refundRequest).execute();
        return defaultResponse.getResponse();
    }

    /**
     * 退款查询(批量)
     *
     * @param refundRequestBuilder 退款参数配置器
     * @return 退款业务响应统一结构
     */
    public static List<RefundResponse> refundList(RefundRequestBuilder refundRequestBuilder) {
        RefundRequest refundRequest = refundRequestBuilder.build();
        DefaultResponse<RefundResponse> defaultResponse = new DefaultPayment<RefundRequest, RefundResponse>(refundRequest).execute();
        return defaultResponse.getResponseList();
    }

    /**
     * 申请转账
     *
     * @param fundRequestBuilder 转账参数配置器
     * @return 转账业务响应统一结构
     */
    public static FundResponse fund(FundRequestBuilder fundRequestBuilder) {
        FundRequest fundRequest = fundRequestBuilder.build();
        DefaultResponse<FundResponse> defaultResponse = new DefaultPayment<FundRequest, FundResponse>(fundRequest).execute();
        return defaultResponse.getResponse();
    }

    /**
     * 申请转账(批量)
     *
     * @param fundRequestBuilder 转账参数配置器
     * @return 转账业务响应统一结构
     */
    public static FundResponse fundBatch(FundRequestBuilder fundRequestBuilder) {
        FundRequest fundRequest = fundRequestBuilder.build();
        DefaultResponse<FundResponse> defaultResponse = new DefaultPayment<FundRequest, FundResponse>(fundRequest).execute();
        return defaultResponse.getResponse();
    }

    /**
     * 转账查询
     *
     * @param fundRequestBuilder 转账参数配置器
     * @return 转账业务响应统一结构
     */
    public static FundResponse fundQuery(FundRequestBuilder fundRequestBuilder) {
        FundRequest fundRequest = fundRequestBuilder.build();
        DefaultResponse<FundResponse> defaultResponse = new DefaultPayment<FundRequest, FundResponse>(fundRequest).execute();
        return defaultResponse.getResponse();
    }

    /**
     * 转账查询(批量)
     *
     * @param fundRequestBuilder 转账参数配置器
     * @return 转账业务响应统一结构
     */
    public static List<FundResponse> fundList(FundRequestBuilder fundRequestBuilder) {
        FundRequest fundRequest = fundRequestBuilder.build();
        DefaultResponse<FundResponse> defaultResponse = new DefaultPayment<FundRequest, FundResponse>(fundRequest).execute();
        return defaultResponse.getResponseList();
    }

}
