package org.lan.iti.common.pay.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.util.ValidationUtils;
import org.lan.iti.common.pay.model.PayModel;
import org.lan.iti.common.pay.util.PayUtils;
import org.springframework.validation.BindException;

import java.util.Map;
import java.util.Objects;

/**
 * @author I'm
 * @since 2020/8/25
 * description 支付凭据
 */
@UtilityClass
@Slf4j
public class Charge {

    private Map<String, String> param;

    public void setOptions(PayModel payModel) throws BindException {
        ValidationUtils.validate(payModel, (Object) null);
        param = payModel.toMap();
    }

    public String createCharge() {
        param.put("sign", PayUtils.sign(PayUtils.getSignCheckContent(param), getParam("privateKey")));
        String res = HttpUtil.post(Objects.requireNonNull(getParam("gatewayHost")), PayUtils.getRequestParamString(param, null));
        return StrUtil.isBlank(res) ? null : res;
    }

    public String queryChargeByOrderNo() {
        if (StrUtil.isBlank(param.get("orderNo")) || StrUtil.isBlank(param.get("gatewayHost"))) {
            return "参数有误";
        }
        param.put("sign", PayUtils.sign(PayUtils.getSignCheckContent(param), getParam("privateKey")));
        String res = HttpUtil.post(Objects.requireNonNull(getParam("gatewayHost")), PayUtils.getRequestParamString(param, null));
        return StrUtil.isBlank(res) ? null : res;
    }

    private String getParam(String key) {
        return param.containsKey(key) && StrUtil.isNotBlank(param.get(key)) ? param.get(key) : null;
    }

}
