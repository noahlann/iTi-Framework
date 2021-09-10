package org.lan.iti.sdk.pay.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lan.iti.sdk.pay.model.IResponse;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author I'm
 * @since 2021/3/27
 * description 转账业务统一响应参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse implements IResponse {

    /**
     * 平台 app id
     */
    private String appId;

    /**
     * 退款额外字段
     */
    private Map<String, Object> extra;

    /**
     * 转账标题
     */
    private String title;

    /**
     * 商户转账单号
     */
    private String outTransferNo;

    /**
     * 平台转账单号
     */
    private String transferNo;

    /**
     * 渠道转账单号
     */
    private String transactionNo;

    /**
     * 转账金额
     */
    private String transferAmount;

    /**
     * 转账开始时间
     */
    private LocalDateTime createTime;

    /**
     * 转账成功时间
     */
    private LocalDateTime transferTime;

    /**
     * 转账状态（转账中:transfering 转账成功:transfered 转账失败:failed 退票:retransfer 状态未知:unknown）
     */
    private String transferStatus;

    /**
     * 错误码
     */
    private String errCode;

    /**
     * 错误信息
     */
    private String errMsg;

    /**
     * 业务码
     */
    private String bizCode;

    /**
     * 转账查询最近api调用时间
     */
    private LocalDateTime currentQueryApiTime;

    /**
     * 转账标题
     */
    public String subject;

    /**
     * 转账描述
     */
    private String description;

    /**
     * 转账到账用户标识
     */
    private String accountId;

    /**
     * 转账到账用户类型
     */
    private String accountType;

    /**
     * 收款方真实姓名
     */
    private String accountName;

    /**
     * 付款渠道：alipay:支付宝 wx:微信支付 upacp:银联支付
     */
    private String channel;

    /**
     * 付款渠道版本
     */
    private String channelVersion;

    /**
     * 银行编号
     */
    private String bankCode;

    /**
     * 是否转账到银行卡
     */
    private Boolean toBank;

    /**
     * 渠道响应信息
     */
    private Map<String, Object> response;

}
