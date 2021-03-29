package org.lan.iti.sdk.pay.payment;

import org.lan.iti.sdk.pay.model.IRequest;
import org.lan.iti.sdk.pay.model.IResponse;

import java.util.Map;

/**
 * @author I'm
 * @since 2021/3/26
 * description 业务执行基类(接口)
 */
public interface IPayment<T extends IRequest, R extends IResponse> {

    /**
     * 执行业务(接口请求)
     *
     * @param map map数据
     * @return 响应数据
     */
    R execute(Map<String, Object> map);
}
