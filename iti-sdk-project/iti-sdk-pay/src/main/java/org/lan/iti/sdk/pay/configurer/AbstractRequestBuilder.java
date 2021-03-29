package org.lan.iti.sdk.pay.configurer;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import org.lan.iti.sdk.pay.model.IRequest;

import java.util.Map;

/**
 * @author I'm
 * @since 2021/3/26
 * description 参数配置基类
 */
public abstract class AbstractRequestBuilder<T extends IRequest,
        Children extends AbstractRequestBuilder<T, Children>> {

    @SuppressWarnings("unchecked")
    protected final Children typedThis = (Children) this;

    protected T request;

    public AbstractRequestBuilder(T request) {
        this.request = request;
    }

    public Children appId(String appId) {
        request.setAppId(appId);
        return typedThis;
    }

    public Children gatewayHost(String gatewayHost) {
        request.setGatewayHost(gatewayHost);
        return typedThis;
    }

    public Children privateKey(String privateKey) {
        request.setPrivateKey(privateKey);
        return typedThis;
    }

    public Children accept(Map<String, Object> modelParams) {
        BeanUtil.mapToBean(modelParams, request.getClass(), false, CopyOptions.create());
        return typedThis;
    }

    public T build() {
        return this.request;
    }

}
