package org.lan.iti.common.pay.service;

import cn.hutool.core.convert.Convert;
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
 * description 支付订单
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
     * 查询支付订单
     *
     * @return 支付订单
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
     * 获取渠道用户信息
     *
     * @return 渠道用户唯一标识
     */
    public String oAuthGetToken() {
        return api(PayConstants.BIZ_CODE_OAUTH_GET_TOKEN);
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
                if (PayUtils.isKeyValueBlankOfMapString(param, PayConstants.OUT_ORDER_NO) ||
                        PayUtils.isKeyValueBlankOfMapString(param, PayConstants.AMOUNT) ||
                        Convert.toFloat(param.get(PayConstants.AMOUNT)) == null ||
                        Convert.toFloat(param.get(PayConstants.AMOUNT)) <= 0 ||
                        PayUtils.isKeyValueBlankOfMapString(param, PayConstants.SUBJECT)
                ) {
                    return PayConstants.PARAM_ERROR;
                }
                break;
            case PayConstants.BIZ_CODE_QUERY:
                if (PayUtils.isKeyValueBlankOfMapString(param, PayConstants.OUT_ORDER_NO)) {
                    return PayConstants.PARAM_ERROR;
                }
                break;
            case PayConstants.BIZ_CODE_REFUND:
                if (PayUtils.isKeyValueBlankOfMapString(param, PayConstants.OUT_ORDER_NO) ||
                        PayUtils.isKeyValueBlankOfMapString(param, PayConstants.OUT_REFUND_NO) ||
                        PayUtils.isKeyValueBlankOfMapString(param, PayConstants.REFUND_AMOUNT) ||
                        Convert.toFloat(param.get(PayConstants.REFUND_AMOUNT)) == null ||
                        Convert.toFloat(param.get(PayConstants.REFUND_AMOUNT)) <= 0
                ) {
                    return PayConstants.PARAM_ERROR;
                }
                break;
            case PayConstants.BIZ_CODE_REFUND_QUERY:
                if (PayUtils.isKeyValueBlankOfMapString(param, PayConstants.OUT_ORDER_NO) ||
                        PayUtils.isKeyValueBlankOfMapString(param, PayConstants.OUT_REFUND_NO)
                ) {
                    return PayConstants.PARAM_ERROR;
                }
                break;
            case PayConstants.BIZ_CODE_FUND:
                if (PayUtils.isKeyValueBlankOfMapString(param, PayConstants.OUT_FUND_NO) ||
                        PayUtils.isKeyValueBlankOfMapString(param, PayConstants.FUND_AMOUNT) ||
                        Convert.toFloat(param.get(PayConstants.FUND_AMOUNT)) == null ||
                        Convert.toFloat(param.get(PayConstants.FUND_AMOUNT)) <= 0 ||
                        PayUtils.isKeyValueBlankOfMapString(param, PayConstants.ACCOUNT_ID) ||
                        PayUtils.isKeyValueBlankOfMapString(param, PayConstants.ACCOUNT_TYPE) ||
                        PayUtils.isKeyValueBlankOfMapString(param, PayConstants.ACCOUNT_NAME) ||
                        PayUtils.isKeyValueBlankOfMapString(param, PayConstants.TO_BANK)
                ) {
                    return PayConstants.PARAM_ERROR;
                }
                break;
            case PayConstants.BIZ_CODE_FUND_QUERY:
                if (
                        PayUtils.isKeyValueBlankOfMapString(param, PayConstants.OUT_FUND_NO)
                ) {
                    return PayConstants.PARAM_ERROR;
                }
                break;
            case PayConstants.BIZ_CODE_OAUTH_GET_TOKEN:
                if (
                        PayUtils.isKeyValueBlankOfMapString(param, PayConstants.AUTH_CODE) ||
                                PayUtils.isKeyValueBlankOfMapString(param, PayConstants.CHANNEL)
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
