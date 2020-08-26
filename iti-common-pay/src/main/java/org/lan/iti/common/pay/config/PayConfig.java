package org.lan.iti.common.pay.config;

import org.lan.iti.common.pay.annotation.NameInMap;
import org.lan.iti.common.pay.model.PayModel;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author I'm
 * @since 2020/8/25
 * description 支付配置
 */
public class PayConfig extends PayModel {

    @NameInMap("appId")
    @NotBlank(message = "appId不能为空！")
    @Pattern(regexp = "^[0-9]+$", message = "appId格式有误！")
    public String appId;

    @NameInMap("privateKey")
    @NotBlank(message = "应用私钥不能为空！")
    public String privateKey;

    @NameInMap("gatewayHost")
    @NotBlank(message = "支付网关不能为空！")
    public String gatewayHost;

}
