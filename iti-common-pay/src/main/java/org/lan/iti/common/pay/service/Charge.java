package org.lan.iti.common.pay.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.util.ValidationUtils;
import org.lan.iti.common.pay.constants.PayConstants;
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
        return api(PayConstants.BIZ_CODE_CREATE_CHARGE);
    }

    /**
     * 查询支付凭据
     *
     * @return 支付凭据
     */
    public String query() {
        return api(PayConstants.BIZ_CODE_QUERY);
    }

    /**
     * 申请退款
     *
     * @return 申请退款
     */
    public String refund() {
        return api(PayConstants.BIZ_CODE_REFUND);
    }

    /**
     * 退款查询
     *
     * @return 退款查询结果信息
     */
    public String refundQuery() {
        return api(PayConstants.BIZ_CODE_REFUND_QUERY);
    }

    /**
     * 转账
     *
     * @return 转账
     */
    public String fund() {
        return api(PayConstants.BIZ_CODE_FUND);
    }

    /**
     * 转账查询
     *
     * @return 转账查询结果信息
     */
    public String fundQuery() {
        return api(PayConstants.BIZ_CODE_FUND_QUERY);
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
    private String api(String bizCode) {
        if (StrUtil.isBlank(param.get(PayConstants.GATEWAY_HOST))) {
            return PayConstants.PARAM_ERROR;
        }
        switch (bizCode) {
            case PayConstants.BIZ_CODE_CREATE_CHARGE:
                if (StrUtil.isBlank(PayConstants.OUT_ORDER_NO) ||
                        StrUtil.isBlank(PayConstants.AMOUNT) ||
                        StrUtil.isBlank(PayConstants.SUBJECT)) {
                    return PayConstants.PARAM_ERROR;
                }
                break;
            case PayConstants.BIZ_CODE_QUERY:
                if (StrUtil.isBlank(PayConstants.OUT_ORDER_NO)) {
                    return PayConstants.PARAM_ERROR;
                }
                break;
            case PayConstants.BIZ_CODE_REFUND:
                if (StrUtil.isBlank(PayConstants.OUT_ORDER_NO) ||
                        StrUtil.isBlank(PayConstants.OUT_REFUND_NO) ||
                        StrUtil.isBlank(PayConstants.REFUND_AMOUNT)) {
                    return PayConstants.PARAM_ERROR;
                }
                break;
            case PayConstants.BIZ_CODE_REFUND_QUERY:
                if (StrUtil.isBlank(PayConstants.OUT_ORDER_NO) ||
                        StrUtil.isBlank(PayConstants.OUT_REFUND_NO)) {
                    return PayConstants.PARAM_ERROR;
                }
                break;
            case PayConstants.BIZ_CODE_FUND:
                if (StrUtil.isBlank(PayConstants.OUT_FUND_NO) ||
                        StrUtil.isBlank(PayConstants.FUND_AMOUNT) ||
                        StrUtil.isBlank(PayConstants.ACCOUNT_ID) ||
                        StrUtil.isBlank(PayConstants.ACCOUNT_TYPE) ||
                        StrUtil.isBlank(PayConstants.ACCOUNT_NAME) ||
                        StrUtil.isBlank(PayConstants.FUND_CHANNEL)
                ) {
                    return PayConstants.PARAM_ERROR;
                }
                break;
            case PayConstants.BIZ_CODE_FUND_QUERY:
                if (StrUtil.isBlank(PayConstants.OUT_FUND_NO)
                ) {
                    return PayConstants.PARAM_ERROR;
                }
                break;
            default:
                break;
        }
        param.put(PayConstants.SIGN, PayUtils.sign(PayUtils.getSignCheckContent(param), getParam(PayConstants.PRIVATE_KEY)));
        String res = HttpUtil.post(Objects.requireNonNull(getParam(PayConstants.GATEWAY_HOST)), PayUtils.getRequestParamString(param, null));
        return StrUtil.isBlank(res) ? null : res;
    }

}
