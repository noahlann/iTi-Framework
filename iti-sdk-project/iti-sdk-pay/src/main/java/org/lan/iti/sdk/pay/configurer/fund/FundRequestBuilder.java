package org.lan.iti.sdk.pay.configurer.fund;


import org.lan.iti.sdk.pay.configurer.AbstractRequestBuilder;
import org.lan.iti.sdk.pay.model.request.FundRequest;


/**
 * @author I'm
 * @since 2021/3/26
 * description 转账参数配置器
 */
public class FundRequestBuilder extends AbstractRequestBuilder<FundRequest, FundRequestBuilder> {

    public FundRequestBuilder() {
        super(new FundRequest());
    }

    /**
     * 设置转账单号
     *
     * @param outFundNo 转账单号
     * @return 转账参数配置器
     */
    public FundRequestBuilder outFundNo(String outFundNo) {
        request.setOutFundNo(outFundNo);
        return this;
    }

    /**
     * 设置转账金额
     *
     * @param fundAmount 转账金额
     * @return 转账参数配置器
     */
    public FundRequestBuilder fundAmount(Float fundAmount) {
        request.setFundAmount(fundAmount);
        return this;
    }

    /**
     * 设置转账渠道
     *
     * @param fundChannel 转账渠道
     * @return 转账参数配置器
     */
    public FundRequestBuilder fundChannel(String fundChannel) {
        request.setFundChannel(fundChannel);
        return this;
    }

    /**
     * 设置是否转账到银行卡
     *
     * @param toBank 是否转账到银行卡
     * @return 转账参数配置器
     */
    public FundRequestBuilder toBank(String toBank) {
        request.setToBank(toBank);
        return this;
    }

    /**
     * 设置转账到账用户标识
     *
     * @param accountId 转账到账用户标识
     * @return 转账参数配置器
     */
    public FundRequestBuilder accountId(String accountId) {
        request.setAccountId(accountId);
        return this;
    }

    /**
     * 设置收款方姓名
     *
     * @param accountName 收款方姓名
     * @return 转账参数配置器
     */
    public FundRequestBuilder accountName(String accountName) {
        request.setAccountName(accountName);
        return this;
    }

    /**
     * 设置转账到账用户类型
     *
     * @param accountType 转账到账用户类型
     * @return 转账参数配置器
     */
    public FundRequestBuilder accountType(String accountType) {
        request.setAccountType(accountType);
        return this;
    }

    /**
     * 设置转账描述
     *
     * @param fundDescription 转账描述
     * @return 转账参数配置器
     */
    public FundRequestBuilder fundDescription(String fundDescription) {
        request.setFundDescription(fundDescription);
        return this;
    }

}
