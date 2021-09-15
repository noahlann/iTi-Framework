package org.lan.iti.sdk.pay.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.lan.iti.sdk.pay.model.response.OrderResponse;

/**
 * @author I'm
 * @date 2021/6/21
 * description 支付异步通知数据
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentNotifyModel extends NotifyModel implements INotifyModel {

    /**
     * 通知信息
     */
    public OrderResponse orderResponse;

}
