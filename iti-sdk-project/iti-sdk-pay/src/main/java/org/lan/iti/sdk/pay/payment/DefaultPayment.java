package org.lan.iti.sdk.pay.payment;

import cn.hutool.core.convert.Convert;
import org.apache.http.HttpEntity;
import org.lan.iti.common.pay.constants.PayConstants;
import org.lan.iti.common.pay.constants.PayFieldKeyConstants;
import org.lan.iti.common.pay.util.PayCommonUtil;
import org.lan.iti.sdk.pay.model.DefaultResponse;
import org.lan.iti.sdk.pay.model.IRequest;
import org.lan.iti.sdk.pay.model.IResponse;
import org.lan.iti.sdk.pay.net.HttpUtil;
import org.lan.iti.sdk.pay.util.PaySdkUtils;

import java.util.Map;

/**
 * @author I'm
 * @since 2021/3/27
 * description 默认业务执行器
 */
public class DefaultPayment<T extends IRequest, R extends IResponse> extends AbstractPayment<T, R> {

    public DefaultPayment(T request) {
        super(request);
    }

    @Override
    protected Map<String, Object> toMap(IRequest model) {
        return super.toMap(model);
    }

    @Override
    protected void enhance(Map<String, Object> params) {
        // 构造签名
        String privateKey = Convert.toStr(params.get(PayFieldKeyConstants.PRIVATE_KEY));
        params.remove(PayFieldKeyConstants.PRIVATE_KEY);
        String sign = PayCommonUtil.sign(PayCommonUtil.getSignCheckContent(params), privateKey);
        params.put(PayFieldKeyConstants.SIGN, sign);
    }

    @Override
    public DefaultResponse<R> execute() {

        Map<String, Object> params = toMap(request);

        enhance(params);

        // http -> request
        HttpEntity httpEntity = HttpUtil.request(Convert.toStr(params.get(PayFieldKeyConstants.GATEWAY_HOST)), params);

        // return response;
        return PaySdkUtils.httpEntity2DefaultResponse(httpEntity);
    }
}
