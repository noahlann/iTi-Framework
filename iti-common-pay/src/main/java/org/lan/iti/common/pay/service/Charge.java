package org.lan.iti.common.pay.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.PatternPool;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.lan.iti.common.core.enums.ErrorLevelEnum;
import org.lan.iti.common.core.exception.ServiceException;
import org.lan.iti.common.core.util.ValidationUtils;
import org.lan.iti.common.pay.constants.PayChannelConstants;
import org.lan.iti.common.pay.constants.PayConstants;
import org.lan.iti.common.pay.model.PayModel;
import org.lan.iti.common.pay.util.PayUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindException;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

import static org.lan.iti.common.pay.constants.PayParamErrorConstants.*;

/**
 * @author I'm
 * @since 2020/8/25
 * description 支付订单
 */
@UtilityClass
@Slf4j
public class Charge {

    String privateKey = null;

    /**
     * 设置配置信息
     *
     * @param payModel 支付模型
     */
    public Map<String, String> setOptions(PayModel payModel) throws BindException {
        ValidationUtils.validate(payModel, (Object) null);
        privateKey = payModel.privateKey;
        payModel.privateKey = null;
        return payModel.toMap();
    }

    /**
     * 创建支付请求
     *
     * @return 支付请求地址
     */
    public String createCharge(Map<String, String> param) {
        return api(PayConstants.BIZ_CODE_CREATE_CHARGE, param);
    }

    /**
     * 查询支付订单
     *
     * @return 支付订单
     */
    public String query(Map<String, String> param) {
        return api(PayConstants.BIZ_CODE_QUERY, param);
    }

    /**
     * 申请退款
     *
     * @return 申请退款
     */
    public String refund(Map<String, String> param) {
        return api(PayConstants.BIZ_CODE_REFUND, param);
    }

    /**
     * 退款查询
     *
     * @return 退款查询结果信息
     */
    public String refundQuery(Map<String, String> param) {
        return api(PayConstants.BIZ_CODE_REFUND_QUERY, param);
    }

    /**
     * 转账
     *
     * @return 转账
     */
    public String fund(Map<String, String> param) {
        return api(PayConstants.BIZ_CODE_FUND, param);
    }

    /**
     * 转账查询
     *
     * @return 转账查询结果信息
     */
    public String fundQuery(Map<String, String> param) {
        return api(PayConstants.BIZ_CODE_FUND_QUERY, param);
    }

    /**
     * 获取渠道用户信息的请求
     *
     * @return 请求地址
     */
    public String oAuthRequest(Map<String, String> param) {
        return api(PayConstants.BIZ_CODE_OAUTH_REQUEST, param);
    }

    /**
     * 获取渠道用户信息
     *
     * @return 渠道用户唯一标识
     */
    public String oAuthGetToken(Map<String, String> param) {
        return api(PayConstants.BIZ_CODE_OAUTH_GET_TOKEN, param);
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
    private String api(String bizCode, Map<String, String> param) {
        if (StrUtil.isBlank(param.get(PayConstants.GATEWAY_HOST))) {
            throw new ServiceException(600, ErrorLevelEnum.PRIMARY.getValue(), GATEWAY_HOST_EMPTY);
        }
        if (!isHttpUrlPatternCorrect(param.get(PayConstants.GATEWAY_HOST))) {
            throw new ServiceException(600, ErrorLevelEnum.PRIMARY.getValue(), GATEWAY_HOST_PATTERN);
        }
        switch (bizCode) {
            case PayConstants.BIZ_CODE_CREATE_CHARGE:
                if (PayUtils.isKeyValueBlankOfMapString(param, PayConstants.OUT_ORDER_NO)) {
                    return OUT_ORDER_EMPTY;
                }
                if (PayUtils.isKeyValueBlankOfMapString(param, PayConstants.AMOUNT) ||
                        StrUtil.isBlank(param.get(PayConstants.AMOUNT))) {
                    return AMOUNT_EMPTY;
                }
                if (!ReUtil.isMatch(org.lan.iti.common.pay.util.PatternPool.MONEY, param.get(PayConstants.AMOUNT)) ||
                        checkAmount(param.get(PayConstants.AMOUNT), PayConstants.MIN_AMOUNT)) {
                    return AMOUNT_PATTERN;
                }
                if (PayUtils.isKeyValueBlankOfMapString(param, PayConstants.SUBJECT)) {
                    return SUBJECT_EMPTY;
                }
                break;
            case PayConstants.BIZ_CODE_QUERY:
                if (PayUtils.isKeyValueBlankOfMapString(param, PayConstants.OUT_ORDER_NO)) {
                    return OUT_ORDER_EMPTY;
                }
                break;
            case PayConstants.BIZ_CODE_REFUND:
                if (PayUtils.isKeyValueBlankOfMapString(param, PayConstants.OUT_ORDER_NO)) {
                    return OUT_ORDER_EMPTY;
                }
                if (PayUtils.isKeyValueBlankOfMapString(param, PayConstants.OUT_REFUND_NO)) {
                    return OUT_REFUND_EMPTY;
                }
                if (PayUtils.isKeyValueBlankOfMapString(param, PayConstants.REFUND_AMOUNT) ||
                        StrUtil.isBlank(param.get(PayConstants.REFUND_AMOUNT))) {
                    return AMOUNT_EMPTY;
                }
                if (!ReUtil.isMatch(org.lan.iti.common.pay.util.PatternPool.MONEY, param.get(PayConstants.REFUND_AMOUNT)) ||
                        checkAmount(param.get(PayConstants.REFUND_AMOUNT), PayConstants.MIN_AMOUNT)) {
                    return AMOUNT_PATTERN;
                }
                break;
            case PayConstants.BIZ_CODE_REFUND_QUERY:
                if (PayUtils.isKeyValueBlankOfMapString(param, PayConstants.OUT_REFUND_NO)) {
                    return OUT_REFUND_EMPTY;
                }
                break;
            case PayConstants.BIZ_CODE_FUND:
                if (PayUtils.isKeyValueBlankOfMapString(param, PayConstants.OUT_FUND_NO)) {
                    return OUT_FUND_EMPTY;
                }
                if (PayUtils.isKeyValueBlankOfMapString(param, PayConstants.FUND_CHANNEL)) {
                    return FUND_CHANNEL_EMPTY;
                }
                if (!StrUtil.equals(param.get(PayConstants.FUND_CHANNEL), PayChannelConstants.ALIPAY) &&
                        !StrUtil.equals(param.get(PayConstants.FUND_CHANNEL), PayChannelConstants.WX) &&
                        !StrUtil.equals(param.get(PayConstants.FUND_CHANNEL), PayChannelConstants.UPACP)
                ) {
                    return FUND_CHANNEL_ERROR;
                }

                if (PayUtils.isKeyValueBlankOfMapString(param, PayConstants.FUND_AMOUNT) ||
                        Convert.toFloat(param.get(PayConstants.FUND_AMOUNT)) == null) {
                    return AMOUNT_EMPTY;
                }
                if (!ReUtil.isMatch(org.lan.iti.common.pay.util.PatternPool.MONEY, param.get(PayConstants.FUND_AMOUNT))) {
                    return AMOUNT_PATTERN;
                }
                if (StrUtil.equals(param.get(PayConstants.FUND_CHANNEL), PayChannelConstants.ALIPAY)) {
                    if (checkAmount(param.get(PayConstants.FUND_AMOUNT), PayConstants.ALIPAY_MIN_FUND_AMOUNT)) {
                        return AMOUNT_PATTERN;
                    }
                } else if (StrUtil.equals(param.get(PayConstants.FUND_CHANNEL), PayChannelConstants.WX)) {
                    if (checkAmount(param.get(PayConstants.FUND_AMOUNT), PayConstants.WX_MIN_FUND_AMOUNT)) {
                        return AMOUNT_PATTERN;
                    }
                }
                if (PayUtils.isKeyValueBlankOfMapString(param, PayConstants.ACCOUNT_ID)) {
                    return ACCOUNT_ID_EMPTY;
                }
                if (PayUtils.isKeyValueBlankOfMapString(param, PayConstants.ACCOUNT_TYPE)) {
                    return ACCOUNT_TYPE_EMPTY;
                }
                if (PayUtils.isKeyValueBlankOfMapString(param, PayConstants.ACCOUNT_NAME)) {
                    return ACCOUNT_NAME_EMPTY;
                }
                if (PayUtils.isKeyValueBlankOfMapString(param, PayConstants.TO_BANK)) {
                    return TO_BANK_EMPTY;
                }
                if (StrUtil.equals(param.get(PayConstants.TO_BANK), PayConstants.TO_BANK_Y) ||
                        StrUtil.equals(param.get(PayConstants.TO_BANK), PayConstants.TO_BANK_N)) {
                    return TO_BANK_PATTERN;
                }
                break;
            case PayConstants.BIZ_CODE_FUND_QUERY:
                if (PayUtils.isKeyValueBlankOfMapString(param, PayConstants.OUT_FUND_NO)) {
                    return OUT_FUND_EMPTY;
                }
                break;
            case PayConstants.BIZ_CODE_OAUTH_GET_TOKEN:
                if (PayUtils.isKeyValueBlankOfMapString(param, PayConstants.AUTH_CODE)) {
                    return AUTH_CODE_EMPTY;
                }
                if (PayUtils.isKeyValueBlankOfMapString(param, PayConstants.CHANNEL)) {
                    return CHANNEL_EMPTY;
                }
                break;
            default:
                break;
        }
        String sign = PayUtils.sign(PayUtils.getSignCheckContent(param), privateKey);
        param.put(PayConstants.SIGN, sign);
        String res;
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
            HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
            httpComponentsClientHttpRequestFactory.setConnectTimeout(8000);
            httpComponentsClientHttpRequestFactory.setReadTimeout(5000);
            restTemplate.setRequestFactory(httpComponentsClientHttpRequestFactory);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
            HttpEntity<String> httpEntity = new HttpEntity<>(PayUtils.getRequestParamString(param, null), headers);
            res = restTemplate.postForObject(Objects.requireNonNull(getParam(PayConstants.GATEWAY_HOST, param)), httpEntity, String.class);
//            res = HttpRequest.post(Objects.requireNonNull(getParam(PayConstants.GATEWAY_HOST, param)))
//                    .body(PayUtils.getRequestParamString(param, null))
//                    .timeout(30000)
//                    .execute()
//                    .body();
        } catch (RestClientException httpException) {
            httpException.printStackTrace();
            res = "请求超时(Read time out)!";
        }
        return StrUtil.isBlank(res) ? null : res;
    }

}
