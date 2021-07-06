package org.lan.iti.sdk.pay.model;

import java.util.Map;

/**
 * @author I'm
 * @date 2021/6/24
 * description 通知模型
 */
public interface INotifyModel {

    /**
     * 获取异步通知签名
     *
     * @return 异步通知签名
     */
    String getSignature();

    /**
     * 获取异步通知地址
     *
     * @return 异步通知地址
     */
    String getNotifyUrl();

    /**
     * 获取异步通知消息体的签名map数据
     *
     * @return 异步通知消息体的签名map数据
     */
    Map<String, Object> getSignMap();

}
