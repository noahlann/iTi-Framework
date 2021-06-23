package org.lan.iti.sdk.pay.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lan.iti.sdk.pay.model.response.OrderResponse;

/**
 * @author I'm
 * @date 2021/6/21
 * description 支付异步通知数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentNotifyModel {

    /**
     * 通知签名
     */
    private String signature;

    /**
     * 异步通知地址
     */
    private String notifyUrl;

    /**
     * 通知信息
     */
    private OrderResponse orderResponse;
}
