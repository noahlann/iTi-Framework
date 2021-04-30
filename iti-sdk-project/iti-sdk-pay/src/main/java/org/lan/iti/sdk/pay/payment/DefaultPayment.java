package org.lan.iti.sdk.pay.payment;

import org.lan.iti.sdk.pay.model.DefaultResponse;
import org.lan.iti.sdk.pay.model.IRequest;
import org.lan.iti.sdk.pay.model.IResponse;

import java.util.Map;

/**
 * @author I'm
 * @since 2021/3/27
 * description 默认业务执行器
 */
public class DefaultPayment<T extends IRequest> extends AbstractPayment<T> {

    public DefaultPayment(T request) {
        super(request);
    }

    @Override
    protected Map<String, Object> toMap(IRequest model) {
        return super.toMap(model);
    }

    @Override
    protected void enhance(Map<String, Object> params) {
        super.enhance(params);
    }

    @Override
    public <R extends IResponse> DefaultResponse<R> execute(Class<R> clazz) {
        return super.execute(clazz);
    }
}
