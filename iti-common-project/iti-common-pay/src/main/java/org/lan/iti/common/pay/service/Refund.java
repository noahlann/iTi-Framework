package org.lan.iti.common.pay.service;

import org.lan.iti.common.pay.constants.PayFieldNameConstants;

import java.util.Map;

/**
 * @author I'm
 * @since 2021/3/25
 * description 退款业务
 */
public class Refund {

    /**
     * 申请退款
     *
     * @return 申请退款
     */
    public String refund(Map<String, Object> param) {
        return Charge.api(PayFieldNameConstants.BIZ_CODE_REFUND, param);
    }

    /**
     * 退款查询
     *
     * @return 退款查询结果信息
     */
    public String refundQuery(Map<String, Object> param) {
        return Charge.api(PayFieldNameConstants.BIZ_CODE_REFUND_QUERY, param);
    }


}
