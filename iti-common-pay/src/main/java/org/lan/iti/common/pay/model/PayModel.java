package org.lan.iti.common.pay.model;


import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Map;

/**
 * @author I'm
 * @since 2020/8/25
 * description 支付配置
 */
@Getter
public class PayModel extends BaseModel {

    /**
     * app id
     */
    @NotBlank(message = "appId不能为空！")
    @Pattern(regexp = "^[0-9]+$", message = "appId格式有误！")
    public String appId;

    /**
     * 应用私钥
     */
    @NotBlank(message = "应用私钥不能为空！")
    public String privateKey;

    /**
     * 请求地址
     */
    @NotBlank(message = "接口网关不能为空！")
    public String gatewayHost;

    /**
     * 商户订单号
     */
    public String outOrderNo;

    /**
     * 金额
     */
    public String amount;

    /**
     * 订单标题
     */
    public String subject;

    /**
     * 订单描述
     */
    public String body;


    /**
     * 商户退款编号
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
     * 商户转账订单号
     */
    public String outFundNo;

    /**
     * 转账到账用户标识
     */
    public String accountId;

    /**
     * 转账到账用户类型
     */
    public String accountType;

    /**
     * 转账金额
     */
    public String fundAmount;

    /**
     * 转账渠道
     */
    public String fundChannel;

    /**
     * 转账描述
     */
    public String fundDescription;

    /**
     * 收款方姓名
     */
    public String accountName;

    /**
     * 是否转账到银行卡（Y:是；N:否）
     */
    public String toBank;

    /**
     * 授权码
     */
    public String authCode;

    /**
     * 渠道编码 支付宝：alipay 微信：wx 银联：upacp
     */
    public String channel;

    /**
     * 微信openid
     */
    public String wxOpenid;

    /**
     * 微信jsapi 支付url
     */
    public String jsApiPayUrl;

}
