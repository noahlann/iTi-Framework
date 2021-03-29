package org.lan.iti.sdk.pay.model.response;

import lombok.Getter;
import org.lan.iti.sdk.pay.model.IResponse;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author I'm
 * @since 2021/3/27
 * description 转账业务统一响应参数
 */
@Getter
public class FundResponse implements IResponse {

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
    private String outFundNo;

    /**
     * 平台转账单号
     */
    private String fundNo;

    /**
     * 渠道转账单号
     */
    private String transactionNo;

    /**
     * 转账金额
     */
    private String fundAmount;

    /**
     * 转账开始时间
     */
    private LocalDateTime createTime;

    /**
     * 转账成功时间
     */
    private LocalDateTime fundTime;

    /**
     * 转账状态（转账中:funding 转账成功:funded 转账失败:failed 退票:refund 状态未知:unknown）
     */
    private String fundStatus;

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
     * 转账渠道：alipay:支付宝 wx:微信支付 upacp:银联支付
     */
    private String fundChannel;

    /**
     * 银行编号
     */
    private String bankCode;

    /**
     * 是否转账到银行卡
     */
    private String toBank;

}
