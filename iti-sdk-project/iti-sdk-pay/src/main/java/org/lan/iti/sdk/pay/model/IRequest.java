package org.lan.iti.sdk.pay.model;

import java.util.Map;

/**
 * @author I'm
 * @since 2021/3/26
 * description 请求参数数据基类
 */
public interface IRequest extends IModel {

    /**
     * 设置 平台 app id
     *
     * @param appId 平台 app id 数据
     */
    void setAppId(String appId);

    /**
     * 设置 请求私钥
     *
     * @param privateKey 接口请求私钥
     */
    void setPrivateKey(String privateKey);

    /**
     * 设置 接口网关
     *
     * @param gatewayHost 接口网关
     */
    void setGatewayHost(String gatewayHost);

    /**
     * 设置 额外字段
     *
     * @param extra 额外字段
     */
    void setExtra(Map<String, Object> extra);

}
