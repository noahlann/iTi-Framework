package org.lan.iti.sdk.pay;

import cn.hutool.http.Method;
import lombok.experimental.UtilityClass;
import org.lan.iti.common.pay.util.SignUtil;
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

    private void init() {
        DEFAULT_PAYMENT_MAP.clear();
        DEFAULT_PAYMENT_MAP.put(OrderRequest.class, new OrderRequestBuilder());
        DEFAULT_PAYMENT_MAP.put(RefundRequest.class, new RefundRequestBuilder());
        DEFAULT_PAYMENT_MAP.put(TransferRequest.class, new TransferRequestBuilder());
    }

    static {
        init();
    }

    @SuppressWarnings("unchecked")
    private static <T extends IRequest, R extends AbstractRequestBuilder<T, R>> R configurer(Class<T> clazz) {
        init();
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
    public static DefaultResponse<OrderResponse> createOrder(OrderRequestBuilder orderRequestBuilder) {
        OrderRequest orderRequest = orderRequestBuilder.build();
        return new DefaultPayment<>(orderRequest).execute(Method.POST.name(), OrderResponse.class);
    }

    /**
     * 创建订单(批量)
     *
     * @param orderRequestBuilder 支付参数配置器
     * @return 支付业务响应统一结构
     */
    public static DefaultResponse<OrderResponse> orderBatch(OrderRequestBuilder orderRequestBuilder) {
        OrderRequest orderRequest = orderRequestBuilder.build();
        return new DefaultPayment<>(orderRequest).execute(Method.POST.name(), OrderResponse.class);
    }

    /**
     * 订单查询
     *
     * @param orderRequestBuilder 支付参数配置器
     * @return 支付业务响应统一结构
     */
    public static DefaultResponse<OrderResponse> orderQuery(OrderRequestBuilder orderRequestBuilder) {
        OrderRequest orderRequest = orderRequestBuilder.build();
        return new DefaultPayment<>(orderRequest).execute(Method.GET.name(), OrderResponse.class);
    }

    /**
     * 订单查询(批量)
     *
     * @param orderRequestBuilder 支付参数配置器
     * @return 支付业务响应统一结构
     */
    public static DefaultResponse<OrderResponse> orderList(OrderRequestBuilder orderRequestBuilder) {
        OrderRequest orderRequest = orderRequestBuilder.build();
        OrderResponse orderResponse = new OrderResponse();
        return new DefaultPayment<>(orderRequest).execute(Method.GET.name(), OrderResponse.class);
    }

    /**
     * 申请退款
     *
     * @param refundRequestBuilder 退款参数配置器
     * @return 退款业务响应统一结构
     */
    public static DefaultResponse<RefundResponse> refund(RefundRequestBuilder refundRequestBuilder) {
        RefundRequest refundRequest = refundRequestBuilder.build();
        return new DefaultPayment<>(refundRequest).execute(Method.POST.name(), RefundResponse.class);
    }

    /**
     * 申请退款
     *
     * @param refundRequestBuilder 退款参数配置器
     * @return 退款业务响应统一结构
     */
    public static DefaultResponse<RefundResponse> refundBatch(RefundRequestBuilder refundRequestBuilder) {
        RefundRequest refundRequest = refundRequestBuilder.build();
        return new DefaultPayment<>(refundRequest).execute(Method.POST.name(), RefundResponse.class);
    }

    /**
     * 退款查询
     *
     * @param refundRequestBuilder 退款参数配置器
     * @return 退款业务响应统一结构
     */
    public static DefaultResponse<RefundResponse> refundQuery(RefundRequestBuilder refundRequestBuilder) {
        RefundRequest refundRequest = refundRequestBuilder.build();
        return new DefaultPayment<>(refundRequest).execute(Method.GET.name(), RefundResponse.class);
    }

    /**
     * 退款查询(批量)
     *
     * @param refundRequestBuilder 退款参数配置器
     * @return 退款业务响应统一结构
     */
    public static DefaultResponse<RefundResponse> refundList(RefundRequestBuilder refundRequestBuilder) {
        RefundRequest refundRequest = refundRequestBuilder.build();
        return new DefaultPayment<>(refundRequest).execute(Method.GET.name(), RefundResponse.class);
    }

    /**
     * 申请转账
     *
     * @param transferRequestBuilder 转账参数配置器
     * @return 转账业务响应统一结构
     */
    public static DefaultResponse<TransferResponse> transfer(TransferRequestBuilder transferRequestBuilder) {
        TransferRequest transferRequest = transferRequestBuilder.build();
        return new DefaultPayment<>(transferRequest).execute(Method.POST.name(), TransferResponse.class);
    }

    /**
     * 申请转账(批量)
     *
     * @param transferRequestBuilder 转账参数配置器
     * @return 转账业务响应统一结构
     */
    public static DefaultResponse<TransferResponse> transferBatch(TransferRequestBuilder transferRequestBuilder) {
        TransferRequest transferRequest = transferRequestBuilder.build();
        return new DefaultPayment<>(transferRequest).execute(Method.POST.name(), TransferResponse.class);
    }

    /**
     * 转账查询
     *
     * @param transferRequestBuilder 转账参数配置器
     * @return 转账业务响应统一结构
     */
    public static DefaultResponse<TransferResponse> transferQuery(TransferRequestBuilder transferRequestBuilder) {
        TransferRequest transferRequest = transferRequestBuilder.build();
        return new DefaultPayment<>(transferRequest).execute(Method.GET.name(), TransferResponse.class);
    }

    /**
     * 转账查询(批量)
     *
     * @param transferRequestBuilder 转账参数配置器
     * @return 转账业务响应统一结构
     */
    public static DefaultResponse<TransferResponse> transferList(TransferRequestBuilder transferRequestBuilder) {
        TransferRequest transferRequest = transferRequestBuilder.build();
        return new DefaultPayment<>(transferRequest).execute(Method.GET.name(), TransferResponse.class);
    }

    /**
     * 异步通知验证
     *
     * @param publicKey 公钥
     * @param method    请求方法
     * @param url       请求地址
     * @param timestamp 请求时间戳
     * @param nonceStr  随机串
     * @param signature 签名
     * @param body      请求参数
     * @return 验证结果
     */
    public static boolean verifyNotify(String publicKey, String method, String url, String timestamp, String nonceStr, String signature, String body) {
        return SignUtil.verify(publicKey, method, url, timestamp, nonceStr, signature, body);
    }


}
