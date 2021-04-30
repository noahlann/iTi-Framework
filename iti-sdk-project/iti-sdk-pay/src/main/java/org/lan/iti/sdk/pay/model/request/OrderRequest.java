package org.lan.iti.sdk.pay.model.request;

import lombok.Data;
import org.lan.iti.sdk.pay.model.IRequest;

import java.util.Map;

/**
 * @author I'm
 * @since 2021/3/26
 * description 支付参数
 */
@Data
public class OrderRequest implements IRequest {

    /**
     * 支付接口网关
     */
    private String gatewayHost;

    /**
     * 平台 app id
     */
    private String appId;

    /**
     * 接口请求私钥
     */
    private String privateKey;

    /**
     * 商户支付单号
     */
    private String outOrderNo;

    /**
     * 订单金额
     */
    private String amount;

    /**
     * 订单标题
     */
    private String subject;

    /**
     * 订单描述
     */
    private String body;

    /**
     * 订单额外字段
     */
    private Map<String, Object> extra;

}
