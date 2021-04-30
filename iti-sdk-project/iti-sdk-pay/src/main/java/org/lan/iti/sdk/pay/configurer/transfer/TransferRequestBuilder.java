package org.lan.iti.sdk.pay.configurer.transfer;


import org.lan.iti.sdk.pay.configurer.AbstractRequestBuilder;
import org.lan.iti.sdk.pay.model.request.TransferRequest;


/**
 * @author I'm
 * @since 2021/3/26
 * description 转账参数配置器
 */
public class TransferRequestBuilder extends AbstractRequestBuilder<TransferRequest, TransferRequestBuilder> {

    public TransferRequestBuilder() {
        super(new TransferRequest());
    }

    /**
     * 设置转账单号
     *
     * @param outFundNo 转账单号
     * @return 转账参数配置器
     */
    public TransferRequestBuilder outFundNo(String outFundNo) {
        request.setOutFundNo(outFundNo);
        return this;
    }

    /**
     * 设置转账金额
     *
     * @param transferAmount 转账金额
     * @return 转账参数配置器
     */
    public TransferRequestBuilder fundAmount(Float transferAmount) {
        request.setFundAmount(transferAmount);
        return this;
    }

    /**
     * 设置转账渠道
     *
     * @param transferChannel 转账渠道
     * @return 转账参数配置器
     */
    public TransferRequestBuilder fundChannel(String transferChannel) {
        request.setFundChannel(transferChannel);
        return this;
    }

    /**
     * 设置是否转账到银行卡
     *
     * @param toBank 是否转账到银行卡
     * @return 转账参数配置器
     */
    public TransferRequestBuilder toBank(String toBank) {
        request.setToBank(toBank);
        return this;
    }

    /**
     * 设置转账到账用户标识
     *
     * @param accountId 转账到账用户标识
     * @return 转账参数配置器
     */
    public TransferRequestBuilder accountId(String accountId) {
        request.setAccountId(accountId);
        return this;
    }

    /**
     * 设置收款方姓名
     *
     * @param accountName 收款方姓名
     * @return 转账参数配置器
     */
    public TransferRequestBuilder accountName(String accountName) {
        request.setAccountName(accountName);
        return this;
    }

    /**
     * 设置转账到账用户类型
     *
     * @param accountType 转账到账用户类型
     * @return 转账参数配置器
     */
    public TransferRequestBuilder accountType(String accountType) {
        request.setAccountType(accountType);
        return this;
    }

    /**
     * 设置转账描述
     *
     * @param transferDescription 转账描述
     * @return 转账参数配置器
     */
    public TransferRequestBuilder fundDescription(String transferDescription) {
        request.setFundDescription(transferDescription);
        return this;
    }

}
