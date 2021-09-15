package org.lan.iti.sdk.pay.model.request;

import lombok.Data;
import org.lan.iti.sdk.pay.model.IRequest;

import java.util.Map;

/**
 * @author I'm
 * @since 2021/3/26
 * description 转账参数
 */
@Data
public class TransferRequest implements IRequest {

    /**
     * 平台 app id
     */
    private String appId;

    /**
     * 转账接口网关
     */
    private String gatewayHost;

    /**
     * 接口请求私钥
     */
    private String privateKey;

    /**
     * 转账单号
     */
    private String outTransferNo;

    /**
     * 付款渠道：alipay:支付宝 wx:微信支付 upacp:银联支付
     */
    private String channel;

    /**
     * 转账金额
     */
    private String transferAmount;

    /**
     * 是否转账到银行卡
     */
    private Boolean toBank;

    /**
     * 转账到账用户标识
     */
    public String accountId;

    /**
     * 转账到账用户类型
     */
    public String accountType;

    /**
     * 收款方姓名
     */
    public String accountName;

    /**
     * 转账标题
     */
    public String subject;

    /**
     * 转账描述
     */
    public String description;

    /**
     * 转账额外字段
     */
    private Map<String, Object> extra;

}
