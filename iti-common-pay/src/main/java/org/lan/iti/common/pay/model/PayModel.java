package org.lan.iti.common.pay.model;


import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

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
     * 订单号
     */
    public String orderNo;

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
     * 退款金额
     */
    public String refundAmount;

}
