package org.lan.iti.sdk.pay.model.request;

import lombok.Data;
import org.lan.iti.sdk.pay.model.IRequest;

import java.util.Map;

/**
 * @author I'm
 * @since 2021/3/26
 * description 退款参数
 */
@Data
public class RefundRequest implements IRequest {

    /**
     * 平台 app id
     */
    private String appId;

    /**
     * 退款接口网关
     */
    private String gatewayHost;

    /**
     * 接口请求私钥
     */
    private String privateKey;

    /**
     * 商户订单号
     */
    private String outOrderNo;

    /**
     * 商户退款单号
     */
    public String outRefundNo;

    /**
     * 退款金额
     */
    public String refundAmount;

    /**
     * 退款描述
     */
    public String refundDescription;

    /**
     * 退款额外字段
     */
    private Map<String, Object> extra;

}
