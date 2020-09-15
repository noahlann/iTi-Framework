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

    /**
     * 设置配置信息
     *
     * @param payModel 支付模型
     */
    public void setOptions(PayModel payModel) throws BindException {
        ValidationUtils.validate(payModel, (Object) null);
        param = payModel.toMap();
    }

    /**
     * 创建支付请求
     *
     * @return 支付请求地址
     */
    public String createCharge() {
        param.put("sign", PayUtils.sign(PayUtils.getSignCheckContent(param), getParam("privateKey")));
        String res = HttpUtil.post(Objects.requireNonNull(getParam("gatewayHost")), PayUtils.getRequestParamString(param, null));
        return StrUtil.isBlank(res) ? null : res;
    }

    /**
     * 查询支付凭据
     *
     * @return 支付凭据
     */
    public String query() {
        return api();
    }

    /**
     * 退款
     *
     * @return 退款信息
     */
    public String refund() {
        return api();
    }

    /**
     * 退款查询
     *
     * @return 退款查询结果信息
     */
    public String refundQuery() {
        return api();
    }

    /**
     * 获取参数值
     *
     * @param key 关键信息
     * @return 参数值
     */
    private String getParam(String key) {
        return param.containsKey(key) && StrUtil.isNotBlank(param.get(key)) ? param.get(key) : null;
    }

    /**
     * 接口调用
     *
     * @return 返回结果
     */
    private String api() {
        if ((StrUtil.isBlank(param.get("orderNo")) && StrUtil.isBlank(param.get("refundNo"))) || StrUtil.isBlank(param.get("gatewayHost"))) {
            return "参数有误!";
        }
        param.put("sign", PayUtils.sign(PayUtils.getSignCheckContent(param), getParam("privateKey")));
        String res = HttpUtil.post(Objects.requireNonNull(getParam("gatewayHost")), PayUtils.getRequestParamString(param, null));
        return StrUtil.isBlank(res) ? null : res;
    }

}
