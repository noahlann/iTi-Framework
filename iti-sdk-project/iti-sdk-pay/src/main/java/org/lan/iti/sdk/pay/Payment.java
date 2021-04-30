package org.lan.iti.sdk.pay;

import lombok.experimental.UtilityClass;
import org.lan.iti.sdk.pay.configurer.AbstractRequestBuilder;
import org.lan.iti.sdk.pay.configurer.payment.OrderRequestBuilder;
import org.lan.iti.sdk.pay.configurer.refund.RefundRequestBuilder;
import org.lan.iti.sdk.pay.configurer.transfer.TransferRequestBuilder;
import org.lan.iti.sdk.pay.model.DefaultResponse;
import org.lan.iti.sdk.pay.model.IRequest;
import org.lan.iti.sdk.pay.model.request.OrderRequest;
import org.lan.iti.sdk.pay.model.request.RefundRequest;
import org.lan.iti.sdk.pay.model.request.TransferRequest;
import org.lan.iti.sdk.pay.model.response.OrderResponse;
import org.lan.iti.sdk.pay.model.response.RefundResponse;
import org.lan.iti.sdk.pay.model.response.TransferResponse;
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
        DEFAULT_PAYMENT_MAP.put(OrderRequest.class, new OrderRequestBuilder());
        DEFAULT_PAYMENT_MAP.put(RefundRequest.class, new RefundRequestBuilder());
        DEFAULT_PAYMENT_MAP.put(TransferRequest.class, new TransferRequestBuilder());
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
    public static OrderRequestBuilder orderConfigurer() {
        return configurer(OrderRequest.class);
    }

    /**
     * 支付参数配置
     *
     * @param orderParamMap 支付数据(map结构)
     * @return 支付参数配置器
     */
    public static OrderRequestBuilder orderConfigurer(Map<String, Object> orderParamMap) {
        OrderRequestBuilder configurer = configurer(OrderRequest.class);
        configurer.accept(orderParamMap);
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
    public static TransferRequestBuilder transferConfigurer() {
        return configurer(TransferRequest.class);
    }

    /**
     * 转账参数配置
     *
     * @param transferParamMap 转账数据(map结构)
     * @return 转账参数配置器
     */
    public static TransferRequestBuilder transferConfigurer(Map<String, Object> transferParamMap) {
        TransferRequestBuilder configurer = configurer(TransferRequest.class);
        configurer.accept(transferParamMap);
        return configurer;
    }

    /**
     * 创建订单
     *
     * @param orderRequestBuilder 支付参数配置器
     * @return 支付业务响应统一结构
     */
    public static OrderResponse createOrder(OrderRequestBuilder orderRequestBuilder) {
        orderRequestBuilder.validate();
        OrderRequest orderRequest = orderRequestBuilder.build();
        DefaultResponse<OrderResponse> defaultResponse = new DefaultPayment<>(orderRequest).execute(OrderResponse.class);
        return defaultResponse.getResponse();
    }

    /**
     * 创建订单(批量)
     *
     * @param orderRequestBuilder 支付参数配置器
     * @return 支付业务响应统一结构
     */
    public static List<OrderResponse> orderBatch(OrderRequestBuilder orderRequestBuilder) {
        OrderRequest orderRequest = orderRequestBuilder.build();
        DefaultResponse<OrderResponse> defaultResponse = new DefaultPayment<>(orderRequest).execute(OrderResponse.class);
        return defaultResponse.getResponseList();
    }

    /**
     * 订单查询
     *
     * @param orderRequestBuilder 支付参数配置器
     * @return 支付业务响应统一结构
     */
    public static OrderResponse orderQuery(OrderRequestBuilder orderRequestBuilder) {
        OrderRequest orderRequest = orderRequestBuilder.build();
        DefaultResponse<OrderResponse> defaultResponse = new DefaultPayment<>(orderRequest).execute(OrderResponse.class);
        return defaultResponse.getResponse();
    }

    /**
     * 订单查询(批量)
     *
     * @param orderRequestBuilder 支付参数配置器
     * @return 支付业务响应统一结构
     */
    public static List<OrderResponse> orderList(OrderRequestBuilder orderRequestBuilder) {
        OrderRequest orderRequest = orderRequestBuilder.build();
        OrderResponse orderResponse = new OrderResponse();
        DefaultResponse<OrderResponse> defaultResponse = new DefaultPayment<>(orderRequest).execute(OrderResponse.class);
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
        DefaultResponse<RefundResponse> defaultResponse = new DefaultPayment<>(refundRequest).execute(RefundResponse.class);
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
        DefaultResponse<RefundResponse> defaultResponse = new DefaultPayment<>(refundRequest).execute(RefundResponse.class);
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
        DefaultResponse<RefundResponse> defaultResponse = new DefaultPayment<>(refundRequest).execute(RefundResponse.class);
        return defaultResponse.getResponse();
    }

    /**
     * 退款查询(批量)
     *
     * @param refundRequestBuilder 退款参数配置器
     * @return 退款业务响应统一结构
     */
    public static RefundResponse refundList(RefundRequestBuilder refundRequestBuilder) {
        RefundRequest refundRequest = refundRequestBuilder.build();
        DefaultResponse<RefundResponse> defaultResponse = new DefaultPayment<>(refundRequest).execute(RefundResponse.class);
        return defaultResponse.getResponse();
    }

    /**
     * 申请转账
     *
     * @param transferRequestBuilder 转账参数配置器
     * @return 转账业务响应统一结构
     */
    public static TransferResponse transfer(TransferRequestBuilder transferRequestBuilder) {
        TransferRequest transferRequest = transferRequestBuilder.build();
        DefaultResponse<TransferResponse> defaultResponse = new DefaultPayment<>(transferRequest).execute(TransferResponse.class);
        return defaultResponse.getResponse();
    }

    /**
     * 申请转账(批量)
     *
     * @param transferRequestBuilder 转账参数配置器
     * @return 转账业务响应统一结构
     */
    public static TransferResponse transferBatch(TransferRequestBuilder transferRequestBuilder) {
        TransferRequest transferRequest = transferRequestBuilder.build();
        DefaultResponse<TransferResponse> defaultResponse = new DefaultPayment<>(transferRequest).execute(TransferResponse.class);
        return defaultResponse.getResponse();
    }

    /**
     * 转账查询
     *
     * @param transferRequestBuilder 转账参数配置器
     * @return 转账业务响应统一结构
     */
    public static TransferResponse fundQuery(TransferRequestBuilder transferRequestBuilder) {
        TransferRequest transferRequest = transferRequestBuilder.build();
        DefaultResponse<TransferResponse> defaultResponse = new DefaultPayment<>(transferRequest).execute(TransferResponse.class);
        return defaultResponse.getResponse();
    }

    /**
     * 转账查询(批量)
     *
     * @param transferRequestBuilder 转账参数配置器
     * @return 转账业务响应统一结构
     */
    public static List<TransferResponse> fundList(TransferRequestBuilder transferRequestBuilder) {
        TransferRequest transferRequest = transferRequestBuilder.build();
        DefaultResponse<TransferResponse> defaultResponse = new DefaultPayment<>(transferRequest).execute(TransferResponse.class);
        return defaultResponse.getResponseList();
    }

}
