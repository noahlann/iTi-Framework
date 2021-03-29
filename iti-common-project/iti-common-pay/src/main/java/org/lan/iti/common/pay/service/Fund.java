package org.lan.iti.common.pay.service;

import org.lan.iti.common.pay.constants.PayFieldNameConstants;

import java.util.Map;

/**
 * @author I'm
 * @since 2021/3/25
 * description
 */
public class Fund {

    /**
     * 转账
     *
     * @return 转账
     */
    public String fund(Map<String, Object> param) {
        return Charge.api(PayFieldNameConstants.BIZ_CODE_FUND, param);
    }

    /**
     * 转账查询
     *
     * @return 转账查询结果信息
     */
    public String fundQuery(Map<String, Object> param) {
        return Charge.api(PayFieldNameConstants.BIZ_CODE_FUND_QUERY, param);
    }


}
