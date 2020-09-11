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

    @NotBlank(message = "appId不能为空！")
    @Pattern(regexp = "^[0-9]+$", message = "appId格式有误！")
    public String appId;

    @NotBlank(message = "应用私钥不能为空！")
    public String privateKey;

    @NotBlank(message = "接口网关不能为空！")
    public String gatewayHost;

    public String orderNo;

    public String amount;

    public String subject;

    public String body;

}
