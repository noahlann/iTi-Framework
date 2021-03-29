package org.lan.iti.sdk.pay.payment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import org.apache.http.HttpEntity;
import org.lan.iti.common.pay.constants.PayFieldKeyConstants;
import org.lan.iti.sdk.pay.model.DefaultResponse;
import org.lan.iti.sdk.pay.model.IRequest;
import org.lan.iti.sdk.pay.model.IResponse;
import org.lan.iti.sdk.pay.net.HttpUtil;
import org.lan.iti.sdk.pay.util.PaySdkUtils;

import java.util.Map;

/**
 * @author I'm
 * @since 2021/3/27
 * description 抽象业务执行器
 */
public abstract class AbstractPayment<T extends IRequest, R extends IResponse> {

    protected final T request;

    public AbstractPayment(T request) {
        this.request = request;
    }

    /**
     * @param model 数据模型
     */
    protected Map<String, Object> toMap(IRequest model) {
        return BeanUtil.beanToMap(model, true, false);
    }

    /**
     * @param params 待扩展的数据
     */
    protected void enhance(Map<String, Object> params) {
    }

    /**
     * 默认处理逻辑
     */
    public DefaultResponse<R> execute() {
        // 默认逻辑
        // request -> map

        Map<String, Object> params = toMap(request);

        enhance(params);
        // http -> request
        HttpEntity httpEntity = HttpUtil.request(Convert.toStr(params.get(PayFieldKeyConstants.GATEWAY_HOST)), params);

        // return response;
        return PaySdkUtils.httpEntity2DefaultResponse(httpEntity);
    }

}
