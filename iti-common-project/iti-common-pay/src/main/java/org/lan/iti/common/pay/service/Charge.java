package org.lan.iti.common.pay.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.pay.constants.PayConstants;
import org.lan.iti.common.pay.constants.PayFieldNameConstants;
import org.lan.iti.common.pay.util.PayCommonUtil;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author I'm
 * @since 2020/8/25
 * description 支付订单
 */
@UtilityClass
@Slf4j
public class Charge {

    /**
     * 创建支付请求
     *
     * @return 支付请求地址
     */
    public String createCharge(Map<String, Object> param) {
        return api(PayFieldNameConstants.BIZ_CODE_CREATE_CHARGE, param);
    }

    /**
     * 查询支付订单
     *
     * @return 支付订单
     */
    public String query(Map<String, Object> param) {
        return api(PayFieldNameConstants.BIZ_CODE_QUERY, param);
    }

    /**
     * 获取参数值
     *
     * @param key 关键信息
     * @return 参数值
     */
    private String getParam(String key, Map<String, String> param) {
        return param.containsKey(key) && StrUtil.isNotBlank(param.get(key)) ? param.get(key) : null;
    }

    /**
     * 检查Http URL格式
     *
     * @param httpUrl Http URL
     * @return 检查结果
     */
    private boolean isHttpUrlPatternCorrect(String httpUrl) {
        return ReUtil.isMatch(PatternPool.URL_HTTP, httpUrl);
    }

    /**
     * 检查金额格式
     *
     * @param amount 金额
     * @return 检查结果
     */
    private boolean checkAmount(String amount, String bizAmount) {
        if (StrUtil.isNotBlank(amount)) {
            BigDecimal amountBigDecimal = new BigDecimal(amount);
            BigDecimal bizAmountBigDecimal = new BigDecimal(bizAmount);
            return amountBigDecimal.compareTo(bizAmountBigDecimal) < PayConstants.BIG_DECIMAL_EQUALS;
        } else {
            return true;
        }
    }

    /**
     * 接口调用
     *
     * @return 返回结果
     */
    public static String api(String bizCode, Map<String, Object> param) {
//        if (StrUtil.isBlank(param.get(PayFieldNameConstants.GATEWAY_HOST))) {
//            throw new ServiceException(600, ErrorLevelEnum.PRIMARY.getValue(), GATEWAY_HOST_EMPTY);
//        }
//        if (!isHttpUrlPatternCorrect(param.get(PayFieldNameConstants.GATEWAY_HOST))) {
//            throw new ServiceException(600, ErrorLevelEnum.PRIMARY.getValue(), GATEWAY_HOST_PATTERN);
//        }
//        switch (bizCode) {
//            case PayFieldNameConstants.BIZ_CODE_CREATE_CHARGE:
//                if (PayCommonUtil.isKeyValueBlankOfMapString(param, PayFieldNameConstants.OUT_ORDER_NO)) {
//                    return OUT_ORDER_EMPTY;
//                }
//                if (PayCommonUtil.isKeyValueBlankOfMapString(param, PayFieldNameConstants.AMOUNT) ||
//                        StrUtil.isBlank(param.get(PayFieldNameConstants.AMOUNT))) {
//                    return AMOUNT_EMPTY;
//                }
//                if (!ReUtil.isMatch(org.lan.iti.common.pay.util.PatternPool.MONEY, param.get(PayFieldNameConstants.AMOUNT)) ||
//                        checkAmount(param.get(PayFieldNameConstants.AMOUNT), PayFieldNameConstants.MIN_AMOUNT)) {
//                    return AMOUNT_PATTERN;
//                }
//                if (PayCommonUtil.isKeyValueBlankOfMapString(param, PayFieldNameConstants.SUBJECT)) {
//                    return SUBJECT_EMPTY;
//                }
//                break;
//            case PayFieldNameConstants.BIZ_CODE_QUERY:
//                if (PayCommonUtil.isKeyValueBlankOfMapString(param, PayFieldNameConstants.OUT_ORDER_NO)) {
//                    return OUT_ORDER_EMPTY;
//                }
//                break;
//            case PayFieldNameConstants.BIZ_CODE_REFUND:
//                if (PayCommonUtil.isKeyValueBlankOfMapString(param, PayFieldNameConstants.OUT_ORDER_NO)) {
//                    return OUT_ORDER_EMPTY;
//                }
//                if (PayCommonUtil.isKeyValueBlankOfMapString(param, PayFieldNameConstants.OUT_REFUND_NO)) {
//                    return OUT_REFUND_EMPTY;
//                }
//                if (PayCommonUtil.isKeyValueBlankOfMapString(param, PayFieldNameConstants.REFUND_AMOUNT) ||
//                        StrUtil.isBlank(param.get(PayFieldNameConstants.REFUND_AMOUNT))) {
//                    return AMOUNT_EMPTY;
//                }
//                if (!ReUtil.isMatch(org.lan.iti.common.pay.util.PatternPool.MONEY, param.get(PayFieldNameConstants.REFUND_AMOUNT)) ||
//                        checkAmount(param.get(PayFieldNameConstants.REFUND_AMOUNT), PayFieldNameConstants.MIN_AMOUNT)) {
//                    return AMOUNT_PATTERN;
//                }
//                break;
//            case PayFieldNameConstants.BIZ_CODE_REFUND_QUERY:
//                if (PayCommonUtil.isKeyValueBlankOfMapString(param, PayFieldNameConstants.OUT_REFUND_NO)) {
//                    return OUT_REFUND_EMPTY;
//                }
//                break;
//            case PayFieldNameConstants.BIZ_CODE_FUND:
//                if (PayCommonUtil.isKeyValueBlankOfMapString(param, PayFieldNameConstants.OUT_FUND_NO)) {
//                    return OUT_FUND_EMPTY;
//                }
//                if (PayCommonUtil.isKeyValueBlankOfMapString(param, PayFieldNameConstants.FUND_CHANNEL)) {
//                    return FUND_CHANNEL_EMPTY;
//                }
//                if (!StrUtil.equals(param.get(PayFieldNameConstants.FUND_CHANNEL), PayChannelConstants.ALIPAY) &&
//                        !StrUtil.equals(param.get(PayFieldNameConstants.FUND_CHANNEL), PayChannelConstants.WX) &&
//                        !StrUtil.equals(param.get(PayFieldNameConstants.FUND_CHANNEL), PayChannelConstants.UPACP)
//                ) {
//                    return FUND_CHANNEL_ERROR;
//                }
//
//                if (PayCommonUtil.isKeyValueBlankOfMapString(param, PayFieldNameConstants.FUND_AMOUNT) ||
//                        Convert.toFloat(param.get(PayFieldNameConstants.FUND_AMOUNT)) == null) {
//                    return AMOUNT_EMPTY;
//                }
//                if (!ReUtil.isMatch(org.lan.iti.common.pay.util.PatternPool.MONEY, param.get(PayFieldNameConstants.FUND_AMOUNT))) {
//                    return AMOUNT_PATTERN;
//                }
//                if (StrUtil.equals(param.get(PayFieldNameConstants.FUND_CHANNEL), PayChannelConstants.ALIPAY)) {
//                    if (checkAmount(param.get(PayFieldNameConstants.FUND_AMOUNT), PayFieldNameConstants.ALIPAY_MIN_FUND_AMOUNT)) {
//                        return AMOUNT_PATTERN;
//                    }
//                } else if (StrUtil.equals(param.get(PayFieldNameConstants.FUND_CHANNEL), PayChannelConstants.WX)) {
//                    if (checkAmount(param.get(PayFieldNameConstants.FUND_AMOUNT), PayFieldNameConstants.WX_MIN_FUND_AMOUNT)) {
//                        return AMOUNT_PATTERN;
//                    }
//                }
//                if (PayCommonUtil.isKeyValueBlankOfMapString(param, PayFieldNameConstants.ACCOUNT_ID)) {
//                    return ACCOUNT_ID_EMPTY;
//                }
//                if (PayCommonUtil.isKeyValueBlankOfMapString(param, PayFieldNameConstants.ACCOUNT_TYPE)) {
//                    return ACCOUNT_TYPE_EMPTY;
//                }
//                if (PayCommonUtil.isKeyValueBlankOfMapString(param, PayFieldNameConstants.ACCOUNT_NAME)) {
//                    return ACCOUNT_NAME_EMPTY;
//                }
//                if (PayCommonUtil.isKeyValueBlankOfMapString(param, PayFieldNameConstants.TO_BANK)) {
//                    return TO_BANK_EMPTY;
//                }
//                if (StrUtil.equals(param.get(PayFieldNameConstants.TO_BANK), PayFieldNameConstants.TO_BANK_Y) ||
//                        StrUtil.equals(param.get(PayFieldNameConstants.TO_BANK), PayFieldNameConstants.TO_BANK_N)) {
//                    return TO_BANK_PATTERN;
//                }
//                break;
//            case PayFieldNameConstants.BIZ_CODE_FUND_QUERY:
//                if (PayCommonUtil.isKeyValueBlankOfMapString(param, PayFieldNameConstants.OUT_FUND_NO)) {
//                    return OUT_FUND_EMPTY;
//                }
//                break;
//            case PayFieldNameConstants.BIZ_CODE_OAUTH_GET_TOKEN:
//                if (PayCommonUtil.isKeyValueBlankOfMapString(param, PayFieldNameConstants.AUTH_CODE)) {
//                    return AUTH_CODE_EMPTY;
//                }
//                if (PayCommonUtil.isKeyValueBlankOfMapString(param, PayFieldNameConstants.CHANNEL)) {
//                    return CHANNEL_EMPTY;
//                }
//                break;
//            default:
//                break;
//        }
        String privateKey = Convert.toStr(param.get(PayFieldNameConstants.PRIVATE_KEY));
        String sign = PayCommonUtil.sign(PayCommonUtil.getSignCheckContent(param), privateKey);
        param.put(PayConstants.SIGN, sign);
        String res = null;
        return StrUtil.isBlank(res) ? null : res;
    }

}
